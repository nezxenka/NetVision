package club.nezxenka.netvision.service.bridge.alert.publisher;

import club.nezxenka.netvision.service.bridge.alert.model.CrossServerAlert;
import club.nezxenka.netvision.service.bridge.connection.RedisManager;
import club.nezxenka.netvision.service.signal.model.SignalType;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;

public class AlertMessagePublisher {

  private static final GsonComponentSerializer SERIALIZER = GsonComponentSerializer.gson();
  private static final Logger LOGGER = Logger.getLogger(AlertMessagePublisher.class.getName());
  private final RedisManager redis;
  private final ObjectMapper mapper;
  private final String origin;
  private final String serverName;

  public AlertMessagePublisher(
      RedisManager redis, ObjectMapper mapper, String origin, String serverName) {
    this.redis = redis;
    this.mapper = mapper;
    this.origin = origin;
    this.serverName = serverName;
  }

  public void publish(SignalType type, Component component, String channel) {
    try {
      String json = SERIALIZER.serialize(component);
      CrossServerAlert alert = new CrossServerAlert(origin, serverName, type.name(), json);
      redis.publishAsync(channel, mapper.writeValueAsString(alert));
    } catch (Exception e) {
      LOGGER.log(Level.FINE, "Failed to publish cross-server alert.", e);
    }
  }
}
