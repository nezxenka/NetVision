package club.nezxenka.netvision.protocol.inbound.transaction;

import club.nezxenka.netvision.actor.model.NetVisionPlayer;
import club.nezxenka.netvision.util.collection.Pair;

public class TransactionResponseMatcher {
  public boolean matchAndConsume(NetVisionPlayer player, short id) {
    Pair<Short, Long> data = null;
    boolean hasID = false;
    for (Pair<Short, Long> it : player.transactionsSent) {
      if (it.first() == id) {
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
