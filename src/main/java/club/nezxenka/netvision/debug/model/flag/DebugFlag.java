package club.nezxenka.netvision.debug.model.flag;

import club.nezxenka.netvision.debug.model.DebugCategory;
import java.util.EnumSet;
import java.util.Set;

public class DebugFlag {
  private final Set<DebugCategory> flags = EnumSet.noneOf(DebugCategory.class);

  public void enable(DebugCategory category) {
    flags.add(category);
  }

  public void disable(DebugCategory category) {
    flags.remove(category);
  }

  public boolean isSet(DebugCategory category) {
    return flags.contains(category);
  }

  public void clear() {
    flags.clear();
  }
}
