package club.nezxenka.netvision.util.latency;

import club.nezxenka.netvision.NetVision;
import club.nezxenka.netvision.player.model.NetVisionPlayer;
import club.nezxenka.netvision.util.message.Message;
import club.nezxenka.netvision.util.message.MessageUtil;
import com.github.retrooper.packetevents.netty.channel.ChannelHelper;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Iterator;

public class LatencyUtils implements ILatencyUtils {
  private record TransactionTask(int transactionId, Runnable task) {}

  private final ArrayDeque<TransactionTask> transactionMap = new ArrayDeque<>();
  private final NetVisionPlayer player;
  private final NetVision plugin;
  private final ArrayList<Runnable> tasksToRun = new ArrayList<>();

  public LatencyUtils(NetVisionPlayer player, NetVision plugin) {
    this.player = player;
    this.plugin = plugin;
  }

  @Override
  public void addRealTimeTask(int transaction, Runnable runnable) {
    addRealTimeTaskInternal(transaction, false, runnable);
  }

  @Override
  public void addRealTimeTaskAsync(int transaction, Runnable runnable) {
    addRealTimeTaskInternal(transaction, true, runnable);
  }

  private void addRealTimeTaskInternal(int transactionId, boolean async, Runnable runnable) {
    if (player.getLastTransactionReceived().get() >= transactionId) {
      if (async) ChannelHelper.runInEventLoop(player.getUser().getChannel(), runnable);
      else runnable.run();
      return;
    }
    synchronized (transactionMap) {
      transactionMap.add(new TransactionTask(transactionId, runnable));
    }
  }

  @Override
  public void handleNettySyncTransaction(int receivedTransactionId) {
    synchronized (transactionMap) {
      tasksToRun.clear();
      Iterator<TransactionTask> iterator = transactionMap.iterator();
      while (iterator.hasNext()) {
        TransactionTask taskEntry = iterator.next();
        int taskTransactionId = taskEntry.transactionId();
        if (receivedTransactionId + 1 < taskTransactionId) break;
        if (receivedTransactionId == taskTransactionId - 1) continue;
        tasksToRun.add(taskEntry.task());
        iterator.remove();
      }
      for (Runnable runnable : tasksToRun) {
        try {
          runnable.run();
        } catch (Exception e) {
          plugin
              .getLogger()
              .severe(
                  "An error occurred when running transactions for player: "
                      + player.getUser().getName());
          e.printStackTrace();
          player.disconnect(MessageUtil.getMessage(Message.INTERNAL_ERROR));
        }
      }
    }
  }
}
