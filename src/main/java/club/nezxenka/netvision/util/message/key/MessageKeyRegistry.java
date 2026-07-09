package club.nezxenka.netvision.util.message.key;

import club.nezxenka.netvision.util.message.Message;
import java.util.EnumMap;
import java.util.Map;

public class MessageKeyRegistry {
  private final Map<Message, String> cache = new EnumMap<>(Message.class);

  public void cache(Message key, String raw) {
    cache.put(key, raw);
  }

  public String get(Message key) {
    return cache.get(key);
  }

  public void clear() {
    cache.clear();
  }
}
