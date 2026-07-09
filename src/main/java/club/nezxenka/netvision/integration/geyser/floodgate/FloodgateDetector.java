package club.nezxenka.netvision.integration.geyser.floodgate;

import java.util.UUID;
import org.geysermc.floodgate.api.FloodgateApi;

public class FloodgateDetector {
  private final boolean available;

  public FloodgateDetector() {
    available = hasClass();
  }

  private boolean hasClass() {
    try {
      Class.forName("org.geysermc.floodgate.api.FloodgateApi");
      return true;
    } catch (ClassNotFoundException e) {
      return false;
    }
  }

  public boolean isBedrock(UUID uuid) {
    if (!available) return false;
    try {
      return FloodgateApi.getInstance().isFloodgatePlayer(uuid);
    } catch (Exception e) {
      return false;
    }
  }
}
