package club.nezxenka.netvision.alert.internal.toggle;

import club.nezxenka.netvision.alert.model.AlertType;
import java.util.Set;
import java.util.UUID;
import org.bukkit.entity.Player;

public class AlertToggleHandler {
  public boolean processToggle(Set<UUID> playerSet, Player player, AlertType type) {
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
