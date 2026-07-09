package club.nezxenka.netvision.data.snapshot;

import club.nezxenka.netvision.data.TickData;
import java.util.ArrayList;
import java.util.List;

public class TickSnapshot {
  public static List<TickData> capture(java.util.Deque<TickData> source) {
    return new ArrayList<>(source);
  }
}
