package club.nezxenka.netvision.entity.compensation.tracker;

import java.util.HashSet;
import java.util.Set;

public class EntityLifecycleTracker {
  private final Set<Integer> pendingSpawns = new HashSet<>();
  private final Set<Integer> pendingDespawns = new HashSet<>();

  public void markForSpawn(int entityId) {
    pendingSpawns.add(entityId);
  }

  public void markForDespawn(int entityId) {
    pendingDespawns.add(entityId);
  }

  public boolean isPendingSpawn(int entityId) {
    return pendingSpawns.contains(entityId);
  }

  public boolean isPendingDespawn(int entityId) {
    return pendingDespawns.contains(entityId);
  }

  public void clearSpawn(int entityId) {
    pendingSpawns.remove(entityId);
  }

  public void clearDespawn(int entityId) {
    pendingDespawns.remove(entityId);
  }

  public void clearAll() {
    pendingSpawns.clear();
    pendingDespawns.clear();
  }
}
