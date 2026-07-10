package club.nezxenka.netvision.protocol.outbound;

import club.nezxenka.netvision.actor.model.NetVisionPlayer;
import club.nezxenka.netvision.actor.state.PlayerRotationData;
import club.nezxenka.netvision.actor.state.PlayerTeleportData;
import com.github.retrooper.packetevents.protocol.teleport.RelativeFlag;
import com.github.retrooper.packetevents.util.Vector3d;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerPlayerPositionAndLook;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerPlayerRotation;

public class TeleportHandler {
  public void handle(WrapperPlayServerPlayerPositionAndLook wrapper, NetVisionPlayer nvPlayer) {
    nvPlayer.sendTransaction();
    int transactionId = nvPlayer.getLastTransactionSent().get();
    Vector3d location = new Vector3d(wrapper.getX(), wrapper.getY(), wrapper.getZ());
    RelativeFlag flags = wrapper.getRelativeFlags();
    nvPlayer.getPendingTeleports().add(new PlayerTeleportData(location, flags, transactionId));
  }

  public void handleRotation(WrapperPlayServerPlayerRotation wrapper, NetVisionPlayer nvPlayer) {
    nvPlayer.sendTransaction();
    int transactionId = nvPlayer.getLastTransactionSent().get();
    nvPlayer
        .getPendingRotations()
        .add(new PlayerRotationData(wrapper.getYaw(), wrapper.getPitch(), transactionId));
  }
}
