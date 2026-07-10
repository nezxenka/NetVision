package club.nezxenka.netvision.actor.manager.registry;

import club.nezxenka.netvision.actor.model.NetVisionPlayer;
import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class PlayerRegistry {
  private final Map<UUID, NetVisionPlayer> players = new ConcurrentHashMap<>();

  public void register(UUID uuid, NetVisionPlayer player) {
    players.put(uuid, player);
  }

  public void unregister(UUID uuid) {
    players.remove(uuid);
  }

  public NetVisionPlayer get(UUID uuid) {
    return players.get(uuid);
  }

  public Collection<NetVisionPlayer> all() {
    return players.values();
  }

  public int count() {
    return players.size();
  }
}
