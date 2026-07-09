package club.nezxenka.netvision.packet.queue;

import club.nezxenka.netvision.player.model.NetVisionPlayer;
import club.nezxenka.netvision.player.state.PlayerRotationData;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerFlying;

public class RotationQueueManager {
  public boolean checkQueue(NetVisionPlayer player, WrapperPlayClientPlayerFlying flying) {
    if (!flying.hasRotationChanged()
        || flying.hasPositionChanged()
        || player.getPendingRotations().isEmpty()) return false;
    PlayerRotationData rotation;
    while ((rotation = player.getPendingRotations().peek()) != null) {
      if (player.getLastTransactionReceived().get() < rotation.getTransactionId()) break;
      if (flying.getLocation().getYaw() == rotation.getYaw()
          && flying.getLocation().getPitch() == rotation.getPitch()) {
        player.getPendingRotations().poll();
        return true;
      }
      if (player.getLastTransactionReceived().get() > rotation.getTransactionId()) {
        player.getPendingRotations().poll();
        continue;
      }
      break;
    }
    return false;
  }
}
