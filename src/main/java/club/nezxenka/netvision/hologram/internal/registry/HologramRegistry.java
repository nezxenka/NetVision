package club.nezxenka.netvision.hologram.internal.registry;

import club.nezxenka.netvision.hologram.display.PlayerHologramRenderer;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class HologramRegistry {
  private final Map<UUID, Map<UUID, PlayerHologramRenderer>> viewerHolograms =
      new ConcurrentHashMap<>();

  public Map<UUID, PlayerHologramRenderer> getOrCreate(UUID viewer) {
    return viewerHolograms.computeIfAbsent(viewer, k -> new ConcurrentHashMap<>());
  }

  public Map<UUID, PlayerHologramRenderer> get(UUID viewer) {
    return viewerHolograms.get(viewer);
  }

  public Map<UUID, PlayerHologramRenderer> remove(UUID viewer) {
    return viewerHolograms.remove(viewer);
  }

  public void forEachTarget(java.util.function.Consumer<PlayerHologramRenderer> action) {
    for (Map<UUID, PlayerHologramRenderer> map : viewerHolograms.values())
      for (PlayerHologramRenderer r : map.values()) action.accept(r);
  }
}
