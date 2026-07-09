package club.nezxenka.netvision.redis.alert;

import club.nezxenka.netvision.NetVision;
import club.nezxenka.netvision.alert.internal.AlertManager;
import club.nezxenka.netvision.alert.model.AlertType;
import club.nezxenka.netvision.config.core.ConfigManager;
import club.nezxenka.netvision.redis.alert.model.CrossServerAlert;
import club.nezxenka.netvision.redis.connection.RedisManager;
import club.nezxenka.netvision.util.message.Message;
import club.nezxenka.netvision.util.message.MessageUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.EnumSet;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;

public class CrossServerAlertService implements CrossServerPublisher {
  private static final String DEFAULT_SERVER_NAME = "server-1";
  private static final String DEFAULT_CHANNEL = "netvision:alerts";
  private static final GsonComponentSerializer COMPONENT_SERIALIZER =
      GsonComponentSerializer.gson();
  private final ConfigManager configManager;
  private final RedisManager redisManager;
  private final AlertManager alertManager;
  private final NetVision plugin;
  private final Logger logger;
  private final String origin = UUID.randomUUID().toString();
  private final ObjectMapper mapper = new ObjectMapper();
  private boolean enabled = false;
  private Set<AlertType> mirroredTypes = EnumSet.noneOf(AlertType.class);
  private String serverName = DEFAULT_SERVER_NAME;
  private String channel = DEFAULT_CHANNEL;

  public CrossServerAlertService(
      ConfigManager configManager,
      RedisManager redisManager,
      AlertManager alertManager,
      NetVision plugin,
      Logger logger) {
    this.configManager = configManager;
    this.redisManager = redisManager;
    this.alertManager = alertManager;
    this.plugin = plugin;
    this.logger = logger;
  }

  public void start() {
    if (!configManager.getConfig().getBoolean("cross-server.enabled", false)) return;
    serverName =
        configManager.getConfig().getString("cross-server.server-name", DEFAULT_SERVER_NAME);
    channel = configManager.getConfig().getString("cross-server.channel", DEFAULT_CHANNEL);
    if (configManager.getConfig().getBoolean("cross-server.alerts.regular", true))
      mirroredTypes.add(AlertType.REGULAR);
    if (configManager.getConfig().getBoolean("cross-server.alerts.suspicious", true))
      mirroredTypes.add(AlertType.SUSPICIOUS);
    if (mirroredTypes.isEmpty()) {
      logger.info("[CrossServer] No alert types selected for mirroring, cross-server disabled.");
      return;
    }
    redisManager.start();
    if (!redisManager.isAvailable()) {
      logger.warning("[CrossServer] Redis unavailable; cross-server alerts disabled.");
      return;
    }
    enabled = true;
    redisManager.subscribe(channel, this::onMessage);
    alertManager.setCrossServerPublisher(this);
    logger.info(
        "[CrossServer] Enabled ("
            + serverName
            + "), mirroring: "
            + mirroredTypes
            + " on channel: "
            + channel);
  }

  @Override
  public void publish(AlertType type, Component component) {
    if (!enabled || !mirroredTypes.contains(type)) return;
    try {
      String componentJson = COMPONENT_SERIALIZER.serialize(component);
      CrossServerAlert alert = new CrossServerAlert(origin, serverName, type.name(), componentJson);
      String payload = mapper.writeValueAsString(alert);
      redisManager.publishAsync(channel, payload);
    } catch (Exception e) {
      logger.log(Level.FINE, "[CrossServer] Failed to publish alert", e);
    }
  }

  private void onMessage(String raw) {
    try {
      CrossServerAlert alert = mapper.readValue(raw, CrossServerAlert.class);
      if (alert.getOrigin().equals(origin)) return;
      AlertType type = AlertType.valueOf(alert.getType());
      if (!mirroredTypes.contains(type)) return;
      Component component = COMPONENT_SERIALIZER.deserialize(alert.getComponent());
      component = component.clickEvent(null);
      Component prefixed =
          MessageUtil.getMessage(Message.CROSS_SERVER_ALERT_PREFIX, "server", alert.getServer())
              .append(Component.space())
              .append(component);
      plugin.getServer().getScheduler().runTask(plugin, () -> alertManager.deliver(prefixed, type));
    } catch (Exception e) {
      logger.log(Level.FINE, "[CrossServer] Failed to process incoming alert", e);
    }
  }

  public void shutdown() {
    enabled = false;
    alertManager.setCrossServerPublisher(null);
  }
}
