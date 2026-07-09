package club.nezxenka.netvision.redis.alert;

import club.nezxenka.netvision.alert.model.AlertType;
import net.kyori.adventure.text.Component;

@FunctionalInterface
public interface CrossServerPublisher {
  void publish(AlertType type, Component component);
}
