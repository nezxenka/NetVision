package club.nezxenka.netvision.engine.model;

import club.nezxenka.netvision.actor.model.NetVisionPlayer;
import java.util.ArrayDeque;
import java.util.Deque;

public class TickCollector {
  private final Deque<TickSample> buffer = new ArrayDeque<>();
  private final int maxSize;

  public TickCollector(int maxSize) {
    this.maxSize = maxSize;
  }

  public void add(NetVisionPlayer player) {
    buffer.add(new TickSample(player));
    while (buffer.size() > maxSize) buffer.removeFirst();
  }

  public Deque<TickSample> getBuffer() {
    return buffer;
  }

  public int size() {
    return buffer.size();
  }

  public void clear() {
    buffer.clear();
  }
}
