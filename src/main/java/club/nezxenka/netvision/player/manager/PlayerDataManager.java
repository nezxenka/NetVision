package club.nezxenka.netvision.player.manager;

import club.nezxenka.netvision.NetVision;
import club.nezxenka.netvision.alert.internal.AlertManager;
import club.nezxenka.netvision.alert.model.AlertType;
import club.nezxenka.netvision.config.core.ConfigManager;
import club.nezxenka.netvision.database.connection.DatabaseManager;
import club.nezxenka.netvision.integration.geyser.GeyserUtil;
import club.nezxenka.netvision.integration.worldguard.WorldGuardManager;
import club.nezxenka.netvision.player.model.NetVisionPlayer;
import club.nezxenka.netvision.server.provider.AIServerProvider;
import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerDataManager implements Listener {
  private final NetVision plugin;
  private final AlertManager alertManager;
  private final ConfigManager configManager;
  private final DatabaseManager databaseManager;
  private final WorldGuardManager worldGuardManager;
  private AIServerProvider aiServerProvider;
  private final Map<UUID, NetVisionPlayer> players = new ConcurrentHashMap<>();

  public PlayerDataManager(
      NetVision plugin,
      AlertManager alertManager,
      ConfigManager configManager,
      DatabaseManager databaseManager,
      AIServerProvider aiServerProvider,
      WorldGuardManager worldGuardManager) {
    this.plugin = plugin;
    this.alertManager = alertManager;
    this.configManager = configManager;
    this.databaseManager = databaseManager;
    this.aiServerProvider = aiServerProvider;
    this.worldGuardManager = worldGuardManager;
    plugin.getServer().getPluginManager().registerEvents(this, plugin);
  }

  @EventHandler
  public void onJoin(PlayerJoinEvent event) {
    Player player = event.getPlayer();
    if (player.hasPermission("netvision.alerts")
        && player.hasPermission("netvision.alerts.enable-on-join")) {
      if (!alertManager.hasAlertsEnabled(player, AlertType.REGULAR))
        alertManager.toggle(player, AlertType.REGULAR, true);
    }
    if (player.hasPermission("netvision.brand")
        && player.hasPermission("netvision.brand.enable-on-join")) {
      if (!alertManager.hasAlertsEnabled(player, AlertType.BRAND))
        alertManager.toggle(player, AlertType.BRAND, true);
    }
    if (player.hasPermission("netvision.exempt")) return;
    NetVisionPlayer nvPlayer =
        new NetVisionPlayer(
            player,
            plugin,
            configManager,
            databaseManager,
            alertManager,
            aiServerProvider,
            worldGuardManager);
    nvPlayer.setBedrock(GeyserUtil.isBedrockPlayer(player.getUniqueId()));
    if (nvPlayer.isBedrockExempt())
      plugin
          .getLogger()
          .info("[Geyser] " + player.getName() + " is a Bedrock player, checks exempted.");
    players.put(player.getUniqueId(), nvPlayer);
    plugin.getChickenCoopMenu().restorePlayer(player.getUniqueId(), player.getName());
  }

  @EventHandler
  public void onQuit(PlayerQuitEvent event) {
    Player player = event.getPlayer();
    UUID uuid = event.getPlayer().getUniqueId();
    plugin.getChickenCoopMenu().removePlayer(player.getUniqueId());
    plugin.getHologramManager().handlePlayerQuit(player);
    alertManager.handlePlayerQuit(player);
    players.remove(uuid);
  }

  public NetVisionPlayer getPlayer(Player player) {
    return player == null ? null : players.get(player.getUniqueId());
  }

  public NetVisionPlayer getPlayer(UUID uuid) {
    return players.get(uuid);
  }

  public Collection<NetVisionPlayer> getPlayers() {
    return players.values();
  }

  public void reloadAllPlayers() {
    for (NetVisionPlayer nvPlayer : players.values()) nvPlayer.reload();
  }
}
