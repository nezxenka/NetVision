package club.nezxenka.netvision.engine.model;

import java.util.ArrayList;
import java.util.List;

public class TickSnapshot {
  public static List<TickSample> capture(java.util.Deque<TickSample> source) {
    return new ArrayList<>(source);
  }
}
