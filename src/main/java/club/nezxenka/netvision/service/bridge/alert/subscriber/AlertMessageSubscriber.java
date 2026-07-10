package club.nezxenka.netvision.service.bridge.alert.subscriber;

import club.nezxenka.netvision.service.bridge.alert.model.CrossServerAlert;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AlertMessageSubscriber {

  private static final Logger LOGGER = Logger.getLogger(AlertMessageSubscriber.class.getName());
  private final ObjectMapper mapper;
  private final String origin;

  public AlertMessageSubscriber(ObjectMapper mapper, String origin) {
    this.mapper = mapper;
    this.origin = origin;
  }

  public void onMessage(String raw, Consumer<CrossServerAlert> handler) {
    try {
      CrossServerAlert alert = mapper.readValue(raw, CrossServerAlert.class);
      if (!alert.getOrigin().equals(origin)) handler.accept(alert);
    } catch (Exception e) {
      LOGGER.log(Level.FINE, "Failed to deserialize cross-server alert.", e);
    }
  }
}
