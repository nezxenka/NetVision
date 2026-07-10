package club.nezxenka.netvision.service.signal.internal.toggle;

import club.nezxenka.netvision.service.signal.model.SignalType;
import java.util.Set;
import java.util.UUID;
import org.bukkit.entity.Player;

public class SignalToggleHandler {
  public boolean processToggle(Set<UUID> playerSet, Player player, SignalType type) {
    UUID uuid = player.getUniqueId();
    if (playerSet.contains(uuid)) {
      playerSet.remove(uuid);
      return false;
    } else {
      playerSet.add(uuid);
      return true;
    }
  }
}
