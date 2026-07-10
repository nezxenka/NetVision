package club.nezxenka.netvision.service.signal.internal.dispatch;

import club.nezxenka.netvision.service.signal.model.SignalType;
import java.util.UUID;
import net.kyori.adventure.text.Component;

public interface SignalDispatchPolicy {
  boolean shouldDeliver(SignalType type, UUID playerUuid);

  Component transformMessage(Component original, SignalType type);
}
