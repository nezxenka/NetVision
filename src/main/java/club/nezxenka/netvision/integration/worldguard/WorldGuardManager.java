package club.nezxenka.netvision.integration.worldguard;

import club.nezxenka.netvision.NetVision;
import club.nezxenka.netvision.core.config.ConfigManager;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import java.util.List;
import java.util.Locale;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class WorldGuardManager {

  private final ConfigManager configManager;
  private final boolean worldGuardLoaded;
  private WorldGuard worldGuardInstance;

  public WorldGuardManager(NetVision plugin, ConfigManager configManager) {
    this.configManager = configManager;
    this.worldGuardLoaded = Bukkit.getPluginManager().isPluginEnabled("WorldGuard");
    if (this.worldGuardLoaded) {
      this.worldGuardInstance = WorldGuard.getInstance();
      plugin.getLogger().info("WorldGuard hook enabled.");
    } else plugin.getLogger().info("WorldGuard not found, hook disabled.");
  }

  public boolean isPlayerInDisabledRegion(Player player) {
    if (!worldGuardLoaded) return false;
    List<String> disabledRegions = configManager.getAiDisabledRegions();
    if (disabledRegions == null || disabledRegions.isEmpty()) return false;
    RegionContainer container = worldGuardInstance.getPlatform().getRegionContainer();
    RegionManager regions = container.get(BukkitAdapter.adapt(player.getWorld()));
    if (regions == null) return false;
    ApplicableRegionSet set =
        regions.getApplicableRegions(
            BlockVector3.at(
                player.getLocation().getX(),
                player.getLocation().getY(),
                player.getLocation().getZ()));
    List<ProtectedRegion> playerRegions =
        set.getRegions().stream().filter(r -> !r.getId().equalsIgnoreCase("__global__")).toList();
    if (playerRegions.isEmpty()) return false;
    final String worldName = player.getWorld().getName().toLowerCase(Locale.ROOT);
    return playerRegions.stream()
        .allMatch(
            region -> {
              final String regionId = region.getId().toLowerCase(Locale.ROOT);
              for (String entry : disabledRegions) {
                if (entry.contains(":")) {
                  String[] parts = entry.split(":", 2);
                  String disabledRegionName = parts[0];
                  String disabledWorldName = parts[1];
                  if (regionId.equals(disabledRegionName) && worldName.equals(disabledWorldName))
                    return true;
                } else {
                  if (regionId.equals(entry)) return true;
                }
              }
              return false;
            });
  }
}
