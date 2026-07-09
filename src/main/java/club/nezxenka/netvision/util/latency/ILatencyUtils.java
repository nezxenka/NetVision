package club.nezxenka.netvision.util.latency;

public interface ILatencyUtils {
  void addRealTimeTask(int transaction, Runnable runnable);

  void addRealTimeTaskAsync(int transaction, Runnable runnable);

  void handleNettySyncTransaction(int receivedTransactionId);
}
