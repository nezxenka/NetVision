package club.nezxenka.netvision.packet.outbound;

import club.nezxenka.netvision.player.model.NetVisionPlayer;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerJoinGame;

public class JoinGameHandler {
  public void handle(WrapperPlayServerJoinGame join, NetVisionPlayer nvPlayer) {
    nvPlayer
        .getLatencyUtils()
        .addRealTimeTask(
            nvPlayer.getLastTransactionSent().get(),
            () -> {
              nvPlayer.setEntityId(join.getEntityId());
              nvPlayer.setGameMode(join.getGameMode());
              nvPlayer.getCompensatedEntities().clear();
            });
  }
}
