package club.nezxenka.netvision.redis.suspicious.fetcher;

import club.nezxenka.netvision.redis.connection.RedisManager;
import club.nezxenka.netvision.redis.suspicious.model.SuspiciousSnapshot;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;

public class RemoteSuspiciousFetcher {
  private final RedisManager redis;
  private final ObjectMapper mapper;
  private final String serverName;

  public RemoteSuspiciousFetcher(RedisManager redis, ObjectMapper mapper, String serverName) {
    this.redis = redis;
    this.mapper = mapper;
    this.serverName = serverName;
  }

  public List<SuspiciousSnapshot> fetch(String keyPrefix) {
    return redis.scanValues(keyPrefix + ":*").stream()
        .map(
            raw -> {
              try {
                return mapper.readValue(raw, SuspiciousSnapshot.class);
              } catch (Exception e) {
                return null;
              }
            })
        .filter(s -> s != null && !s.getServer().equals(serverName))
        .toList();
  }
}
