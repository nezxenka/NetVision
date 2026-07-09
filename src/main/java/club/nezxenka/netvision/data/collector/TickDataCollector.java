package club.nezxenka.netvision.data.collector;

import club.nezxenka.netvision.data.TickData;
import club.nezxenka.netvision.player.model.NetVisionPlayer;
import java.util.ArrayDeque;
import java.util.Deque;

public class TickDataCollector {
  private final Deque<TickData> buffer = new ArrayDeque<>();
  private final int maxSize;

  public TickDataCollector(int maxSize) {
    this.maxSize = maxSize;
  }

  public void add(NetVisionPlayer player) {
    buffer.add(new TickData(player));
    while (buffer.size() > maxSize) buffer.removeFirst();
  }

  public Deque<TickData> getBuffer() {
    return buffer;
  }

  public int size() {
    return buffer.size();
  }

  public void clear() {
    buffer.clear();
  }
}
