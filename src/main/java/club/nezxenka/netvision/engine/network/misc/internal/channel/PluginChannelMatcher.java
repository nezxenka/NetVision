package club.nezxenka.netvision.engine.network.misc.channel;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.manager.server.ServerVersion;

public class PluginChannelMatcher {
  private static final String MODERN_CHANNEL = "minecraft:brand";
  private static final String LEGACY_CHANNEL = "MC|Brand";

  public String resolveChannel() {
    return PacketEvents.getAPI()
            .getServerManager()
            .getVersion()
            .isNewerThanOrEquals(ServerVersion.V_1_13)
        ? MODERN_CHANNEL
        : LEGACY_CHANNEL;
  }

  public boolean matches(String received, String expected) {
    return received.equals(expected);
  }
}
