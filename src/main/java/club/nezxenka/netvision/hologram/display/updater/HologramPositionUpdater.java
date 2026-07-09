package club.nezxenka.netvision.hologram.display.updater;

import eu.decentsoftware.holograms.api.DHAPI;
import eu.decentsoftware.holograms.api.holograms.Hologram;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class HologramPositionUpdater {
  public void updatePosition(Hologram hologram, Player target) {
    Location loc =
        new Location(
            target.getWorld(),
            target.getLocation().getX(),
            target.getLocation().getY() + 3.0,
            target.getLocation().getZ());
    DHAPI.moveHologram(hologram, loc);
  }
}
