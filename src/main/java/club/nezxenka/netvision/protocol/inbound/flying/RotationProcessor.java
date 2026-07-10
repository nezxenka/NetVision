package club.nezxenka.netvision.protocol.inbound.flying;

import club.nezxenka.netvision.actor.model.NetVisionPlayer;
import club.nezxenka.netvision.util.rotation.RotationUpdate;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerFlying;

public class RotationProcessor {
  public void process(NetVisionPlayer player, WrapperPlayClientPlayerFlying packet) {
    boolean ignore =
        player.packetStateData.lastPacketWasOnePointSeventeenDuplicate
            && player.packetStateData.ignoreDuplicatePacketRotation;
    if (packet.hasPositionChanged()) {
      player.x = packet.getLocation().getX();
      player.y = packet.getLocation().getY();
      player.z = packet.getLocation().getZ();
      player.packetStateData.lastClaimedPosition = packet.getLocation().getPosition();
    }
    if (packet.hasRotationChanged() && !ignore) {
      float newYaw = packet.getLocation().getYaw();
      float newPitch = packet.getLocation().getPitch();
      float deltaYaw = newYaw - player.yaw;
      float deltaPitch = newPitch - player.pitch;
      RotationUpdate update = player.rotationUpdate;
      update.getFrom().setYaw(player.yaw);
      update.getFrom().setPitch(player.pitch);
      update.getTo().setYaw(newYaw);
      update.getTo().setPitch(newPitch);
      update.setDeltaYaw(deltaYaw);
      update.setDeltaPitch(deltaPitch);
      player.getModuleCoordinator().onRotationUpdate(update);
      player.lastYaw = player.yaw;
      player.lastPitch = player.pitch;
      player.yaw = newYaw;
      player.pitch = newPitch;
    }
  }
}
