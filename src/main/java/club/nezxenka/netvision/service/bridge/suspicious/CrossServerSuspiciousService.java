package club.nezxenka.netvision.service.bridge.suspicious;

import club.nezxenka.netvision.NetVision;
import club.nezxenka.netvision.actor.manager.PlayerDataManager;
import club.nezxenka.netvision.actor.model.NetVisionPlayer;
import club.nezxenka.netvision.core.config.ConfigManager;
import club.nezxenka.netvision.engine.network.neural.NeuralAnalyzer;
import club.nezxenka.netvision.service.bridge.connection.RedisManager;
import club.nezxenka.netvision.service.bridge.suspicious.model.SuspiciousSnapshot;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class CrossServerSuspiciousService {
  private static final String DEFAULT_SERVER_NAME = "server-1";
  private static final String DEFAULT_CHANNEL = "netvision:alerts";
  private static final long DEFAULT_TTL_SECONDS = 30L;
  private static final long DEFAULT_REFRESH_SECONDS = 10L;
  private final ConfigManager configManager;
  private final RedisManager redisManager;
  private final PlayerDataManager playerDataManager;
  private final NetVision plugin;
  private final Logger logger;
  private final ObjectMapper mapper = new ObjectMapper();
  private boolean enabled = false;
  private String serverName = DEFAULT_SERVER_NAME;
  private String keyPrefix = DEFAULT_CHANNEL + ":suspect";
  private long ttlSeconds = DEFAULT_TTL_SECONDS;
  private int taskId = -1;

  public CrossServerSuspiciousService(
      ConfigManager configManager,
      RedisManager redisManager,
      PlayerDataManager playerDataManager,
      NetVision plugin,
      Logger logger) {
    this.configManager = configManager;
    this.redisManager = redisManager;
    this.playerDataManager = playerDataManager;
    this.plugin = plugin;
    this.logger = logger;
  }

  public boolean isActive() {
    return enabled;
  }

  public void start() {
    if (!configManager.getConfig().getBoolean("cross-server.enabled", false)
        || !configManager.getConfig().getBoolean("cross-server.alerts.suspicious", true)) return;
    serverName =
        configManager.getConfig().getString("cross-server.server-name", DEFAULT_SERVER_NAME);
    String channel = configManager.getConfig().getString("cross-server.channel", DEFAULT_CHANNEL);
    keyPrefix = channel + ":suspect";
    ttlSeconds =
        configManager
            .getConfig()
            .getLong("cross-server.suspicious-sync.ttl-seconds", DEFAULT_TTL_SECONDS);
    long refreshSeconds =
        Math.max(
            1L,
            Math.min(
                configManager
                    .getConfig()
                    .getLong(
                        "cross-server.suspicious-sync.refresh-seconds", DEFAULT_REFRESH_SECONDS),
                ttlSeconds));
    ttlSeconds = Math.max(ttlSeconds, refreshSeconds + 1L);
    if (!redisManager.isAvailable()) {
      logger.warning(
          "[CrossServer] suspicious-sync enabled but Redis unavailable; list stays local.");
      return;
    }
    enabled = true;
    long periodTicks = refreshSeconds * 20L;
    taskId =
        Bukkit.getScheduler()
            .runTaskTimerAsynchronously(
                plugin, this::publishLocalSuspicious, periodTicks, periodTicks)
            .getTaskId();
    logger.info(
        "[CrossServer] Sharing suspicious players as \""
            + serverName
            + "\" (refresh "
            + refreshSeconds
            + "s, ttl "
            + ttlSeconds
            + "s).");
  }

  private void publishLocalSuspicious() {
    if (!enabled) return;
    for (NetVisionPlayer nvPlayer : playerDataManager.getPlayers()) publishPlayer(nvPlayer);
  }

  private void publishPlayer(NetVisionPlayer nvPlayer) {
    NeuralAnalyzer check = nvPlayer.getModuleCoordinator().getModule(NeuralAnalyzer.class);
    if (check == null || check.getBuffer() <= 0.0) return;
    Player player = nvPlayer.getPlayer();
    try {
      SuspiciousSnapshot snapshot =
          new SuspiciousSnapshot(
              serverName,
              nvPlayer.getUuid().toString(),
              player.getName(),
              check.getBuffer(),
              player.getPing(),
              System.currentTimeMillis());
      String payload = mapper.writeValueAsString(snapshot);
      redisManager.setWithTtl(
          keyPrefix + ":" + serverName + ":" + nvPlayer.getUuid(), payload, ttlSeconds);
    } catch (Exception e) {
      logger.log(Level.FINE, "[CrossServer] Failed to publish suspect " + player.getName(), e);
    }
  }

  public List<SuspiciousSnapshot> fetchRemote() {
    if (!enabled) return List.of();
    return redisManager.scanValues(keyPrefix + ":*").stream()
        .map(
            raw -> {
              try {
                return mapper.readValue(raw, SuspiciousSnapshot.class);
              } catch (Exception e) {
                logger.log(Level.FINE, "[CrossServer] Bad suspect payload.", e);
                return null;
              }
            })
        .filter(s -> s != null && !s.getServer().equals(serverName))
        .toList();
  }

  public void shutdown() {
    enabled = false;
    if (taskId != -1) {
      Bukkit.getScheduler().cancelTask(taskId);
      taskId = -1;
    }
  }
}
