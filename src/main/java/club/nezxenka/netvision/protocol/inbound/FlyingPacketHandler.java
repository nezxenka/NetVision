package club.nezxenka.netvision.protocol.inbound;

import club.nezxenka.netvision.actor.model.NetVisionPlayer;
import club.nezxenka.netvision.util.rotation.RotationUpdate;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerFlying;

public class FlyingPacketHandler {
  public static void processRotation(
      NetVisionPlayer nvPlayer, WrapperPlayClientPlayerFlying packet) {
    boolean ignoreRotation =
        nvPlayer.packetStateData.lastPacketWasOnePointSeventeenDuplicate
            && nvPlayer.packetStateData.ignoreDuplicatePacketRotation;
    if (packet.hasPositionChanged()) {
      nvPlayer.x = packet.getLocation().getX();
      nvPlayer.y = packet.getLocation().getY();
      nvPlayer.z = packet.getLocation().getZ();
      nvPlayer.packetStateData.lastClaimedPosition = packet.getLocation().getPosition();
    }
    if (packet.hasRotationChanged() && !ignoreRotation) {
      float newYaw = packet.getLocation().getYaw();
      float newPitch = packet.getLocation().getPitch();
      float deltaYaw = newYaw - nvPlayer.yaw;
      float deltaPitch = newPitch - nvPlayer.pitch;
      RotationUpdate update = nvPlayer.rotationUpdate;
      update.getFrom().setYaw(nvPlayer.yaw);
      update.getFrom().setPitch(nvPlayer.pitch);
      update.getTo().setYaw(newYaw);
      update.getTo().setPitch(newPitch);
      update.setDeltaYaw(deltaYaw);
      update.setDeltaPitch(deltaPitch);
      nvPlayer.getModuleCoordinator().onRotationUpdate(update);
      nvPlayer.lastYaw = nvPlayer.yaw;
      nvPlayer.lastPitch = nvPlayer.pitch;
      nvPlayer.yaw = newYaw;
      nvPlayer.pitch = newPitch;
    }
  }
}
