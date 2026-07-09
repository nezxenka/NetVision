package club.nezxenka.netvision.integration.worldguard.region;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import java.util.List;
import org.bukkit.entity.Player;

public class RegionQueryService {
  private final WorldGuard worldGuard;

  public RegionQueryService(WorldGuard worldGuard) {
    this.worldGuard = worldGuard;
  }

  public List<ProtectedRegion> getRegionsAt(Player player) {
    RegionContainer container = worldGuard.getPlatform().getRegionContainer();
    RegionManager regions = container.get(BukkitAdapter.adapt(player.getWorld()));
    if (regions == null) return List.of();
    ApplicableRegionSet set =
        regions.getApplicableRegions(
            BlockVector3.at(
                player.getLocation().getX(),
                player.getLocation().getY(),
                player.getLocation().getZ()));
    return set.getRegions().stream()
        .filter(r -> !r.getId().equalsIgnoreCase("__global__"))
        .toList();
  }
}
