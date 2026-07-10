package club.nezxenka.netvision.protocol.outbound;

import club.nezxenka.netvision.actor.model.NetVisionPlayer;

public class RespawnHandler {
  public void handle(NetVisionPlayer nvPlayer) {
    nvPlayer
        .getLatencyUtils()
        .addRealTimeTask(
            nvPlayer.getLastTransactionSent().get(),
            () -> nvPlayer.getCompensatedEntities().clear());
  }
}
