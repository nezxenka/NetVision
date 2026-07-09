package club.nezxenka.netvision.command.impl.history.session;

import club.nezxenka.netvision.menu.history.model.HistorySession;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class HistorySessionManager {
  private final Map<UUID, HistorySession> sessions = new ConcurrentHashMap<>();

  public void register(UUID viewer, HistorySession session) {
    sessions.put(viewer, session);
  }

  public HistorySession get(UUID viewer) {
    return sessions.get(viewer);
  }

  public void remove(UUID viewer) {
    sessions.remove(viewer);
  }
}
