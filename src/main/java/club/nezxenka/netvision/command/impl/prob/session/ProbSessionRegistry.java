package club.nezxenka.netvision.command.impl.prob.session;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class ProbSessionRegistry {
  private final Map<UUID, ProbSessionEntry> sessions = new ConcurrentHashMap<>();

  public record ProbSessionEntry(UUID targetUuid, org.bukkit.scheduler.BukkitTask task) {}

  public void register(UUID viewer, ProbSessionEntry entry) {
    sessions.put(viewer, entry);
  }

  public ProbSessionEntry get(UUID viewer) {
    return sessions.get(viewer);
  }

  public ProbSessionEntry remove(UUID viewer) {
    return sessions.remove(viewer);
  }

  public boolean contains(UUID viewer) {
    return sessions.containsKey(viewer);
  }
}
