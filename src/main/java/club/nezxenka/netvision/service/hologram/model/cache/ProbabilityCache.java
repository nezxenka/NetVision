package club.nezxenka.netvision.service.hologram.model.cache;

import club.nezxenka.netvision.service.hologram.model.ProbabilityHistory;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class ProbabilityCache {
  private final Map<UUID, ProbabilityHistory> cache = new ConcurrentHashMap<>();

  public void add(UUID uuid, double probability) {
    cache.computeIfAbsent(uuid, k -> new ProbabilityHistory()).add(probability);
  }

  public ProbabilityHistory get(UUID uuid) {
    return cache.getOrDefault(uuid, new ProbabilityHistory());
  }

  public void remove(UUID uuid) {
    cache.remove(uuid);
  }
}
