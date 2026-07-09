package club.nezxenka.netvision.packet.inbound;

import club.nezxenka.netvision.player.model.NetVisionPlayer;
import club.nezxenka.netvision.util.collection.Pair;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPong;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientWindowConfirmation;

public class TransactionPacketHandler {
  public boolean handle(PacketReceiveEvent event, NetVisionPlayer nvPlayer) {
    short id;
    if (event.getPacketType() == PacketType.Play.Client.WINDOW_CONFIRMATION) {
      WrapperPlayClientWindowConfirmation transaction =
          new WrapperPlayClientWindowConfirmation(event);
      id = transaction.getActionId();
      if (id <= 0 && addResponse(nvPlayer, id)) event.setCancelled(true);
      return true;
    } else if (event.getPacketType() == PacketType.Play.Client.PONG) {
      WrapperPlayClientPong pong = new WrapperPlayClientPong(event);
      id = (short) pong.getId();
      if (addResponse(nvPlayer, id)) event.setCancelled(true);
      return true;
    }
    return false;
  }

  private boolean addResponse(NetVisionPlayer player, short id) {
    Pair<Short, Long> data = null;
    boolean hasID = false;
    for (Pair<Short, Long> iterator : player.transactionsSent) {
      if (iterator.first() == id) {
        hasID = true;
        break;
      }
    }
    if (hasID) {
      do {
        data = player.transactionsSent.poll();
        if (data == null) break;
        player.getLastTransactionReceived().incrementAndGet();
      } while (data.first() != id);
      player
          .getLatencyUtils()
          .handleNettySyncTransaction(player.getLastTransactionReceived().get());
    }
    return data != null;
  }
}
