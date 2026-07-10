package club.nezxenka.netvision.actor.state;

import club.nezxenka.netvision.util.collection.Pair;
import it.unimi.dsi.fastutil.ints.IntArraySet;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class TransactionStateTracker {
  public final Queue<Pair<Short, Long>> transactionsSent = new ConcurrentLinkedQueue<>();
  public final IntArraySet entitiesDespawnedThisTransaction = new IntArraySet();
  public final Set<Short> didWeSendThatTrans = ConcurrentHashMap.newKeySet();
  public final AtomicInteger lastTransactionSent = new AtomicInteger(0);
  public final AtomicInteger lastTransactionReceived = new AtomicInteger(0);
  private final AtomicInteger transactionIDCounter = new AtomicInteger(0);

  public short nextTransactionId() {
    return (short) (-1 * (transactionIDCounter.getAndIncrement() & 0x7FFF));
  }
}
