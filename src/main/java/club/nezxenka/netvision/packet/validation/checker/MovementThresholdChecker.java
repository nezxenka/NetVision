package club.nezxenka.netvision.packet.validation.checker;

import club.nezxenka.netvision.player.model.NetVisionPlayer;
import com.github.retrooper.packetevents.protocol.player.ClientVersion;
import com.github.retrooper.packetevents.util.Vector3d;

public class MovementThresholdChecker {
  public boolean isWithinThreshold(
      NetVisionPlayer player,
      Vector3d lastClaimed,
      com.github.retrooper.packetevents.protocol.world.Location current) {
    double threshold = player.getMovementThreshold();
    return player.getUser().getClientVersion().isNewerThanOrEquals(ClientVersion.V_1_17)
        && lastClaimed.distanceSquared(current.getPosition()) < threshold * threshold;
  }

  public boolean isNewVersion(NetVisionPlayer player) {
    return player.getUser().getClientVersion().isNewerThanOrEquals(ClientVersion.V_1_21);
  }
}
