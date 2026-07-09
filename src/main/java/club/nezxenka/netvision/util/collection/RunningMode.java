package club.nezxenka.netvision.util.collection;

import it.unimi.dsi.fastutil.doubles.Double2IntMap;
import it.unimi.dsi.fastutil.doubles.Double2IntOpenHashMap;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import lombok.Getter;

public class RunningMode {
  private static final double threshold = 1e-3;
  private final Queue<Double> addList;
  private final Double2IntMap popularityMap = new Double2IntOpenHashMap();
  @Getter private final int maxSize;

  public RunningMode(int maxSize) {
    if (maxSize == 0) throw new IllegalArgumentException("There's no mode to a size 0 list!");
    this.addList = new ArrayBlockingQueue<>(maxSize);
    this.maxSize = maxSize;
  }

  public int size() {
    return addList.size();
  }

  public void add(double value) {
    pop();
    for (Double2IntMap.Entry entry : popularityMap.double2IntEntrySet()) {
      if (Math.abs(entry.getDoubleKey() - value) < threshold) {
        entry.setValue(entry.getIntValue() + 1);
        addList.add(entry.getDoubleKey());
        return;
      }
    }
    popularityMap.put(value, 1);
    addList.add(value);
  }

  private void pop() {
    if (addList.size() >= maxSize) {
      double type = addList.poll();
      int popularity = popularityMap.get(type);
      if (popularity == 1) popularityMap.remove(type);
      else popularityMap.put(type, popularity - 1);
    }
  }

  public Pair<Double, Integer> getMode() {
    int max = 0;
    Double mostPopular = null;
    for (Double2IntMap.Entry entry : popularityMap.double2IntEntrySet()) {
      if (entry.getIntValue() > max) {
        max = entry.getIntValue();
        mostPopular = entry.getDoubleKey();
      }
    }
    return new Pair<>(mostPopular, max);
  }
}
