package club.nezxenka.netvision.alert.internal.dispatch;

import club.nezxenka.netvision.alert.model.AlertType;
import java.util.UUID;
import net.kyori.adventure.text.Component;

public interface AlertDispatchPolicy {
  boolean shouldDeliver(AlertType type, UUID playerUuid);

  Component transformMessage(Component original, AlertType type);
}
