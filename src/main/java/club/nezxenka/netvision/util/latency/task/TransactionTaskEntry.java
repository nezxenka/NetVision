package club.nezxenka.netvision.util.latency.task;

public class TransactionTaskEntry implements Comparable<TransactionTaskEntry> {
  private final int transactionId;
  private final Runnable task;

  public TransactionTaskEntry(int transactionId, Runnable task) {
    this.transactionId = transactionId;
    this.task = task;
  }

  public int transactionId() {
    return transactionId;
  }

  public Runnable task() {
    return task;
  }

  @Override
  public int compareTo(TransactionTaskEntry o) {
    return Integer.compare(this.transactionId, o.transactionId);
  }
}
