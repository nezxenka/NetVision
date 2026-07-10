package club.nezxenka.netvision.visual.menu.coop.model.cache;

import club.nezxenka.netvision.visual.menu.coop.model.PlayerRiskData;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

public class RiskDataCache {

  private final LinkedHashMap<UUID, PlayerRiskData> cache;
  private final int maxSize;

  public RiskDataCache(int maxSize) {
    this.maxSize = maxSize;
    this.cache = new LinkedHashMap<>();
  }

  public void put(UUID uuid, PlayerRiskData data) {
    if (cache.size() >= maxSize) {
      var it = cache.keySet().iterator();
      if (it.hasNext()) {
        it.next();
        it.remove();
      }
    }
    cache.put(uuid, data);
  }

  public PlayerRiskData get(UUID uuid) {
    return cache.get(uuid);
  }

  public void remove(UUID uuid) {
    cache.remove(uuid);
  }

  public Map<UUID, PlayerRiskData> all() {
    return cache;
  }
}
