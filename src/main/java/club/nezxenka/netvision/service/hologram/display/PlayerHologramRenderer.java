package club.nezxenka.netvision.service.hologram.display;

import club.nezxenka.netvision.service.hologram.internal.HologramManager;
import club.nezxenka.netvision.service.hologram.model.ProbabilityHistory;
import eu.decentsoftware.holograms.api.DHAPI;
import eu.decentsoftware.holograms.api.holograms.Hologram;
import java.util.Collections;
import java.util.List;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class PlayerHologramRenderer {
  private final HologramManager manager;
  private final Player viewer;
  private final Player target;
  private final String hologramId;
  private Hologram hologram;
  private boolean spawned = false;

  public PlayerHologramRenderer(HologramManager manager, Player viewer, Player target) {
    this.manager = manager;
    this.viewer = viewer;
    this.target = target;
    this.hologramId =
        "netvision_"
            + viewer.getUniqueId().toString().replace("-", "")
            + "_"
            + target.getUniqueId().toString().replace("-", "");
  }

  public void spawn() {
    if (spawned || !target.isOnline() || !viewer.isOnline()) return;
    Location loc =
        new Location(
            target.getWorld(),
            target.getLocation().getX(),
            target.getLocation().getY() + 3.0,
            target.getLocation().getZ());
    hologram = DHAPI.createHologram(hologramId, loc, false);
    DHAPI.addHologramLine(hologram, "&c0.00");
    DHAPI.addHologramLine(hologram, "&7AVG: &a0.00000");
    hologram.setDefaultVisibleState(false);
    hologram.setShowPlayer(viewer);
    spawned = true;
  }

  public void update() {
    if (!spawned || hologram == null || !target.isOnline() || !viewer.isOnline()) {
      remove();
      return;
    }
    ProbabilityHistory history = manager.getHistory(target.getUniqueId());
    List<Double> probs = history.getAll();
    double avg = history.getAverage();
    if (probs.isEmpty()) {
      probs = Collections.singletonList(0.0);
      avg = 0.0;
    }
    Location targetLoc = target.getLocation();
    Location holoLoc =
        new Location(
            targetLoc.getWorld(), targetLoc.getX(), targetLoc.getY() + 3.0, targetLoc.getZ());
    DHAPI.moveHologram(hologram, holoLoc);
    DHAPI.setHologramLine(hologram, 0, buildProbLine(probs));
    DHAPI.setHologramLine(hologram, 1, buildAvgLine(avg));
  }

  public void remove() {
    if (!spawned) return;
    if (hologram != null) {
      hologram.delete();
      hologram = null;
    }
    spawned = false;
  }

  private String buildProbLine(List<Double> probs) {
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < probs.size(); i++) {
      double p = probs.get(i);
      sb.append(colorCode(p)).append(String.format("%.2f", p));
      if (i < probs.size() - 1) sb.append("&f ");
    }
    return sb.toString();
  }

  private String buildAvgLine(double avg) {
    return "&7AVG: " + colorCode(avg) + String.format("%.5f", avg);
  }

  private String colorCode(double p) {
    if (p > 0.9) return "&c";
    if (p > 0.5) return "&e";
    return "&a";
  }

  public boolean isSpawned() {
    return spawned;
  }
}
