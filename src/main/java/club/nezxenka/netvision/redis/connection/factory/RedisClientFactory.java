package club.nezxenka.netvision.redis.connection.factory;

import io.lettuce.core.ClientOptions;
import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import io.lettuce.core.SocketOptions;
import java.time.Duration;

public class RedisClientFactory {
  public RedisClient create(RedisURI uri, long timeoutSeconds) {
    RedisClient client = RedisClient.create(uri);
    client.setOptions(
        ClientOptions.builder()
            .disconnectedBehavior(ClientOptions.DisconnectedBehavior.REJECT_COMMANDS)
            .socketOptions(
                SocketOptions.builder().connectTimeout(Duration.ofSeconds(timeoutSeconds)).build())
            .build());
    return client;
  }

  public RedisURI.Builder uriBuilder(
      String host, int port, int database, boolean ssl, long timeoutSec) {
    return RedisURI.builder()
        .withHost(host)
        .withPort(port)
        .withDatabase(database)
        .withSsl(ssl)
        .withTimeout(Duration.ofSeconds(timeoutSec));
  }
}
