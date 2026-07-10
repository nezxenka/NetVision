package club.nezxenka.netvision.service.bridge.alert;

import club.nezxenka.netvision.service.signal.model.SignalType;
import net.kyori.adventure.text.Component;

@FunctionalInterface
public interface CrossServerPublisher {
  void publish(SignalType type, Component component);
}
