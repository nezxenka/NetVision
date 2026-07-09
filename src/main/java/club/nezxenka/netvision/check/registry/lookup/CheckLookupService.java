package club.nezxenka.netvision.check.registry.lookup;

import club.nezxenka.netvision.check.api.Check;
import java.util.Map;

public class CheckLookupService {
  private final Map<Class<? extends Check>, Check> registry;

  public CheckLookupService(Map<Class<? extends Check>, Check> registry) {
    this.registry = registry;
  }

  @SuppressWarnings("unchecked")
  public <T extends Check> T find(Class<T> type) {
    return (T) registry.get(type);
  }
}
