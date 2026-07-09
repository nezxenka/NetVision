package club.nezxenka.netvision.packet.outbound;

import club.nezxenka.netvision.player.model.NetVisionPlayer;

public class RespawnHandler {
  public void handle(NetVisionPlayer nvPlayer) {
    nvPlayer
        .getLatencyUtils()
        .addRealTimeTask(
            nvPlayer.getLastTransactionSent().get(),
            () -> nvPlayer.getCompensatedEntities().clear());
  }
}
