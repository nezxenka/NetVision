package club.nezxenka.netvision.engine.coordinator;

import club.nezxenka.netvision.engine.api.AnalysisModule;
import java.util.Map;

public class ModuleLookup {
  private final Map<Class<? extends AnalysisModule>, AnalysisModule> registry;

  public ModuleLookup(Map<Class<? extends AnalysisModule>, AnalysisModule> registry) {
    this.registry = registry;
  }

  @SuppressWarnings("unchecked")
  public <T extends AnalysisModule> T find(Class<T> type) {
    return (T) registry.get(type);
  }
}
