package club.nezxenka.netvision.remote.connection.timeout;

public class TimeoutManager {
  private static volatile long waitingUntil;

  public boolean isBlocked() {
    return System.currentTimeMillis() < waitingUntil;
  }

  public void block(long millis) {
    waitingUntil = System.currentTimeMillis() + millis;
  }

  public static void reset() {
    waitingUntil = 0;
  }
}
