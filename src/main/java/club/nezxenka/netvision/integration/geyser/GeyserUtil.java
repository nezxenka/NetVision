package club.nezxenka.netvision.integration.geyser;

import java.util.UUID;
import org.geysermc.floodgate.api.FloodgateApi;

public final class GeyserUtil {
  private static final String FLOODGATE_API_CLASS = "org.geysermc.floodgate.api.FloodgateApi";
  private static final String GEYSER_API_CLASS = "org.geysermc.geyser.api.GeyserApi";
  private static final String BEDROCK_UUID_PREFIX = "00000000-0000-0000-0009";
  private static final boolean floodgatePresent;
  private static final boolean geyserPresent;

  static {
    floodgatePresent = hasClass(FLOODGATE_API_CLASS);
    geyserPresent = hasClass(GEYSER_API_CLASS);
  }

  private GeyserUtil() {}

  public static boolean isBedrockPlayer(UUID uuid) {
    return isFloodgateBedrock(uuid)
        || isGeyserBedrock(uuid)
        || uuid.toString().startsWith(BEDROCK_UUID_PREFIX);
  }

  private static boolean isFloodgateBedrock(UUID uuid) {
    if (!floodgatePresent) return false;
    try {
      return FloodgateApi.getInstance().isFloodgatePlayer(uuid);
    } catch (Exception e) {
      return false;
    }
  }

  private static boolean isGeyserBedrock(UUID uuid) {
    if (!geyserPresent) return false;
    try {
      Class<?> geyserApiClass = Class.forName(GEYSER_API_CLASS);
      Object api = geyserApiClass.getMethod("api").invoke(null);
      return (boolean) api.getClass().getMethod("isBedrockPlayer", UUID.class).invoke(api, uuid);
    } catch (Exception e) {
      return false;
    }
  }

  private static boolean hasClass(String name) {
    try {
      Class.forName(name);
      return true;
    } catch (ClassNotFoundException e) {
      return false;
    }
  }
}
