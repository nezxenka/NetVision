package club.nezxenka.netvision.engine.network.misc.validator;

import club.nezxenka.netvision.actor.model.NetVisionPlayer;
import com.github.retrooper.packetevents.protocol.player.ClientVersion;
import java.nio.charset.StandardCharsets;

public class BrandDataValidator {

  public boolean hasReachExploit(NetVisionPlayer player) {
    return (player.getBrand().contains("forge")
        && player.getUser().getClientVersion().isNewerThanOrEquals(ClientVersion.V_1_18_2)
        && player.getUser().getClientVersion().isOlderThan(ClientVersion.V_1_19_4));
  }

  public boolean isValidLength(byte[] data) {
    return data.length <= 64 && data.length > 0;
  }

  public String extractBrand(byte[] data) {
    byte[] brandBytes = new byte[data.length - 1];
    System.arraycopy(data, 1, brandBytes, 0, brandBytes.length);
    return new String(brandBytes, StandardCharsets.UTF_8).replace(" (Velocity)", "");
  }
}
