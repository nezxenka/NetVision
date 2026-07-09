package club.nezxenka.netvision.command.impl.status.toggler;

import club.nezxenka.netvision.hologram.internal.HologramManager;
import org.bukkit.entity.Player;

public class HologramToggleService {
  private final HologramManager manager;

  public HologramToggleService(HologramManager manager) {
    this.manager = manager;
  }

  public boolean toggleAll(Player viewer) {
    boolean hasAny = false;
    for (Player target : org.bukkit.Bukkit.getOnlinePlayers()) {
      if (manager.isEnabled(viewer, target)) {
        hasAny = true;
        break;
      }
    }
    if (hasAny) manager.disableForAll(viewer);
    else manager.enableForAll(viewer);
    return !hasAny;
  }

  public boolean toggleOne(Player viewer, Player target) {
    if (target.hasPermission("netvision.exempt")) return false;
    if (manager.isEnabled(viewer, target)) {
      manager.disableHologram(viewer, target);
      return false;
    } else {
      manager.enableHologram(viewer, target);
      return true;
    }
  }
}
