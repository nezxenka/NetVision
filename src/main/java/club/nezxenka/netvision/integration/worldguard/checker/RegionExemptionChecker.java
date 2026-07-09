package club.nezxenka.netvision.integration.worldguard.checker;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import java.util.List;
import java.util.Locale;

public class RegionExemptionChecker {
  public boolean isAllExempt(
      List<ProtectedRegion> playerRegions, List<String> disabledEntries, String worldName) {
    if (playerRegions.isEmpty() || disabledEntries.isEmpty()) return false;
    return playerRegions.stream()
        .allMatch(
            region ->
                matchesAny(region.getId().toLowerCase(Locale.ROOT), disabledEntries, worldName));
  }

  private boolean matchesAny(String regionId, List<String> entries, String worldName) {
    for (String entry : entries) {
      if (entry.contains(":")) {
        String[] parts = entry.split(":", 2);
        if (regionId.equals(parts[0]) && worldName.equals(parts[1])) return true;
      } else if (regionId.equals(entry)) return true;
    }
    return false;
  }
}
