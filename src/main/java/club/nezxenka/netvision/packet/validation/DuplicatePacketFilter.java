package club.nezxenka.netvision.packet.validation;

import club.nezxenka.netvision.player.model.NetVisionPlayer;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.player.ClientVersion;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerFlying;

public class DuplicatePacketFilter {
  public void filterDuplicate(
      NetVisionPlayer player, WrapperPlayClientPlayerFlying flying, PacketReceiveEvent event) {
    if (player.packetStateData.lastPacketWasTeleport) return;
    if (player.getUser().getClientVersion().isNewerThanOrEquals(ClientVersion.V_1_21)) return;
    final com.github.retrooper.packetevents.protocol.world.Location location = flying.getLocation();
    final double threshold = player.getMovementThreshold();
    final boolean inVehicle = player.getCompensatedEntities().self.getRiding() != null;
    if (!player.packetStateData.lastPacketWasTeleport
        && flying.hasPositionChanged()
        && flying.hasRotationChanged()
        && ((flying.isOnGround() == player.packetStateData.packetPlayerOnGround
                && (player.getUser().getClientVersion().isNewerThanOrEquals(ClientVersion.V_1_17)
                    && player.packetStateData.lastClaimedPosition.distanceSquared(
                            location.getPosition())
                        < threshold * threshold))
            || inVehicle)) {
      if (player.isCancelDuplicatePacket()) event.setCancelled(true);
      player.packetStateData.lastPacketWasOnePointSeventeenDuplicate = true;
      if (!player.packetStateData.ignoreDuplicatePacketRotation) {
        if (player.yaw != location.getYaw() || player.pitch != location.getPitch()) {
          player.lastYaw = player.yaw;
          player.lastPitch = player.pitch;
        }
        player.yaw = location.getYaw();
        player.pitch = location.getPitch();
      }
      player.packetStateData.lastClaimedPosition = location.getPosition();
    }
  }
}
