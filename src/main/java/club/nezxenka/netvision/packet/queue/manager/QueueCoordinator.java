package club.nezxenka.netvision.packet.queue.manager;

public class QueueCoordinator {
  public boolean isReady(int lastReceived, int transactionId) {
    return lastReceived >= transactionId;
  }

  public boolean isExpired(int lastReceived, int transactionId) {
    return lastReceived > transactionId;
  }
}
