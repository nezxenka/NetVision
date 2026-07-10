package club.nezxenka.netvision.protocol.outbound.teleport;

import club.nezxenka.netvision.actor.model.NetVisionPlayer;
import club.nezxenka.netvision.actor.state.PlayerRotationData;
import club.nezxenka.netvision.actor.state.PlayerTeleportData;
import com.github.retrooper.packetevents.protocol.teleport.RelativeFlag;
import com.github.retrooper.packetevents.util.Vector3d;

public class TeleportTrigger {
  public void queuePositionLook(
      NetVisionPlayer player, double x, double y, double z, RelativeFlag flags) {
    player.sendTransaction();
    int tid = player.getLastTransactionSent().get();
    player.getPendingTeleports().add(new PlayerTeleportData(new Vector3d(x, y, z), flags, tid));
  }

  public void queueRotation(NetVisionPlayer player, float yaw, float pitch) {
    player.sendTransaction();
    int tid = player.getLastTransactionSent().get();
    player.getPendingRotations().add(new PlayerRotationData(yaw, pitch, tid));
  }
}
