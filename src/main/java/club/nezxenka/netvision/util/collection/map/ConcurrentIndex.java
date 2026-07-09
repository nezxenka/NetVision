package club.nezxenka.netvision.util.collection.map;

import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

public class ConcurrentIndex<K, V> {
  private final ConcurrentHashMap<K, V> map = new ConcurrentHashMap<>();

  public V computeIfAbsent(K key, Function<K, V> factory) {
    return map.computeIfAbsent(key, factory);
  }

  public V get(K key) {
    return map.get(key);
  }

  public void put(K key, V value) {
    map.put(key, value);
  }

  public void remove(K key) {
    map.remove(key);
  }

  public int size() {
    return map.size();
  }
}
