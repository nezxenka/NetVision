package club.nezxenka.netvision.integration.geyser.detector;

import java.util.UUID;

public class BedrockDetector {
  private static final String BEDROCK_UUID_PREFIX = "00000000-0000-0000-0009";

  public boolean isPrefixMatch(UUID uuid) {
    return uuid.toString().startsWith(BEDROCK_UUID_PREFIX);
  }

  public boolean hasClass(String name) {
    try {
      Class.forName(name);
      return true;
    } catch (ClassNotFoundException e) {
      return false;
    }
  }
}
