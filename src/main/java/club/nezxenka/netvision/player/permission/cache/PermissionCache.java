package club.nezxenka.netvision.player.permission.cache;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class PermissionCache {
  private final Map<UUID, Boolean> exemptCache = new ConcurrentHashMap<>();
  private final Map<UUID, Boolean> alertAutoEnableCache = new ConcurrentHashMap<>();
  private final Map<UUID, Boolean> brandAutoEnableCache = new ConcurrentHashMap<>();

  public void setExempt(UUID uuid, boolean value) {
    exemptCache.put(uuid, value);
  }

  public boolean isExempt(UUID uuid) {
    return exemptCache.getOrDefault(uuid, false);
  }

  public void setAlertAuto(UUID uuid, boolean value) {
    alertAutoEnableCache.put(uuid, value);
  }

  public boolean isAlertAuto(UUID uuid) {
    return alertAutoEnableCache.getOrDefault(uuid, false);
  }

  public void setBrandAuto(UUID uuid, boolean value) {
    brandAutoEnableCache.put(uuid, value);
  }

  public boolean isBrandAuto(UUID uuid) {
    return brandAutoEnableCache.getOrDefault(uuid, false);
  }

  public void clear(UUID uuid) {
    exemptCache.remove(uuid);
    alertAutoEnableCache.remove(uuid);
    brandAutoEnableCache.remove(uuid);
  }
}
