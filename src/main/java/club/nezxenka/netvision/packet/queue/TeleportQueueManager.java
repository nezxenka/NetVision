package club.nezxenka.netvision.packet.queue;

import club.nezxenka.netvision.player.model.NetVisionPlayer;
import club.nezxenka.netvision.player.state.PlayerTeleportData;
import com.github.retrooper.packetevents.protocol.teleport.RelativeFlag;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerFlying;

public class TeleportQueueManager {
  public boolean checkQueue(NetVisionPlayer player, WrapperPlayClientPlayerFlying flying) {
    if (!flying.hasPositionChanged() || player.getPendingTeleports().isEmpty()) return false;
    PlayerTeleportData teleport;
    while ((teleport = player.getPendingTeleports().peek()) != null) {
      if (player.getLastTransactionReceived().get() < teleport.getTransactionId()) break;
      com.github.retrooper.packetevents.protocol.world.Location flyingLocation =
          flying.getLocation();
      RelativeFlag flags = teleport.getFlags();
      double expectedX =
          flags.has(RelativeFlag.X)
              ? player.x + teleport.getLocation().getX()
              : teleport.getLocation().getX();
      double expectedY =
          flags.has(RelativeFlag.Y)
              ? player.y + teleport.getLocation().getY()
              : teleport.getLocation().getY();
      double expectedZ =
          flags.has(RelativeFlag.Z)
              ? player.z + teleport.getLocation().getZ()
              : teleport.getLocation().getZ();
      final double epsilon = 1.0E-7;
      if (Math.abs(flyingLocation.getX() - expectedX) < epsilon
          && Math.abs(flyingLocation.getY() - expectedY) < epsilon
          && Math.abs(flyingLocation.getZ() - expectedZ) < epsilon) {
        player.getPendingTeleports().poll();
        return true;
      }
      if (player.getLastTransactionReceived().get() > teleport.getTransactionId()) {
        player.getPendingTeleports().poll();
        continue;
      }
      break;
    }
    return false;
  }
}
