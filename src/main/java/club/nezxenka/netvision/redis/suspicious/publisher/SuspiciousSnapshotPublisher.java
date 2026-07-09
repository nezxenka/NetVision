package club.nezxenka.netvision.redis.suspicious.publisher;

import club.nezxenka.netvision.check.packet.ai.internal.AICheck;
import club.nezxenka.netvision.player.model.NetVisionPlayer;
import club.nezxenka.netvision.redis.connection.RedisManager;
import club.nezxenka.netvision.redis.suspicious.model.SuspiciousSnapshot;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.entity.Player;

public class SuspiciousSnapshotPublisher {

  private static final Logger LOGGER =
      Logger.getLogger(SuspiciousSnapshotPublisher.class.getName());
  private final RedisManager redis;
  private final ObjectMapper mapper;
  private final String serverName;

  public SuspiciousSnapshotPublisher(RedisManager redis, ObjectMapper mapper, String serverName) {
    this.redis = redis;
    this.mapper = mapper;
    this.serverName = serverName;
  }

  public void publish(NetVisionPlayer nvPlayer, AICheck check, String keyPrefix, long ttl) {
    Player player = nvPlayer.getPlayer();
    try {
      SuspiciousSnapshot snap =
          new SuspiciousSnapshot(
              serverName,
              nvPlayer.getUuid().toString(),
              player.getName(),
              check.getBuffer(),
              player.getPing(),
              System.currentTimeMillis());
      redis.setWithTtl(
          keyPrefix + ":" + serverName + ":" + nvPlayer.getUuid(),
          mapper.writeValueAsString(snap),
          ttl);
    } catch (Exception e) {
      LOGGER.log(Level.FINE, "Failed to publish suspicious snapshot.", e);
    }
  }
}
