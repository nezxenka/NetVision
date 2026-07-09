package club.nezxenka.netvision.redis.connection;

import club.nezxenka.netvision.config.core.ConfigManager;
import io.lettuce.core.ClientOptions;
import io.lettuce.core.KeyScanCursor;
import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import io.lettuce.core.ScanArgs;
import io.lettuce.core.SetArgs;
import io.lettuce.core.SocketOptions;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.pubsub.RedisPubSubAdapter;
import io.lettuce.core.pubsub.StatefulRedisPubSubConnection;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RedisManager {

  private static final String DEFAULT_HOST = "localhost";
  private static final int DEFAULT_PORT = 6379;
  private static final int DEFAULT_DATABASE = 0;
  private static final long DEFAULT_TIMEOUT_SECONDS = 10L;
  private static final long SCAN_BATCH = 256L;
  private static final Duration SHUTDOWN_TIMEOUT = Duration.ofSeconds(2);
  private final ConfigManager configManager;
  private final Logger logger;
  private boolean attempted = false;
  private RedisClient client;
  private StatefulRedisConnection<String, String> connection;
  private StatefulRedisPubSubConnection<String, String> pubSubConnection;
  private volatile boolean available = false;

  public RedisManager(ConfigManager configManager, Logger logger) {
    this.configManager = configManager;
    this.logger = logger;
  }

  public boolean isAvailable() {
    return available;
  }

  public void start() {
    if (attempted) return;
    attempted = true;
    if (!configManager.getConfig().getBoolean("redis.enabled", false)) return;
    String host = configManager.getConfig().getString("redis.host", DEFAULT_HOST);
    int port = configManager.getConfig().getInt("redis.port", DEFAULT_PORT);
    int database = configManager.getConfig().getInt("redis.database", DEFAULT_DATABASE);
    boolean useSsl = configManager.getConfig().getBoolean("redis.ssl", false);
    long timeoutSeconds =
        Math.max(
            1L,
            configManager.getConfig().getLong("redis.timeout-seconds", DEFAULT_TIMEOUT_SECONDS));
    String password = configManager.getConfig().getString("redis.password", "");
    RedisURI.Builder uriBuilder =
        RedisURI.builder()
            .withHost(host)
            .withPort(port)
            .withDatabase(database)
            .withSsl(useSsl)
            .withTimeout(Duration.ofSeconds(timeoutSeconds));
    if (!password.isEmpty()) uriBuilder.withPassword(password.toCharArray());
    try {
      RedisClient redisClient = RedisClient.create(uriBuilder.build());
      redisClient.setOptions(
          ClientOptions.builder()
              .disconnectedBehavior(ClientOptions.DisconnectedBehavior.REJECT_COMMANDS)
              .socketOptions(
                  SocketOptions.builder()
                      .connectTimeout(Duration.ofSeconds(timeoutSeconds))
                      .build())
              .build());
      this.client = redisClient;
      this.connection = redisClient.connect();
      this.pubSubConnection = redisClient.connectPubSub();
      this.available = true;
      logger.info("[Redis] Connected to " + host + ":" + port + " (database " + database + ").");
    } catch (Exception e) {
      logger.log(
          Level.WARNING,
          "[Redis] Could not connect to "
              + host
              + ":"
              + port
              + "; cross-server features are disabled.",
          e);
      shutdown();
    }
  }

  public void publishAsync(String channel, String message) {
    if (!available || connection == null) return;
    try {
      connection
          .async()
          .publish(channel, message)
          .exceptionally(
              error -> {
                logger.log(Level.FINE, "[Redis] Publish to " + channel + " failed.", error);
                return 0L;
              });
    } catch (Exception e) {
      logger.log(Level.FINE, "[Redis] Publish to " + channel + " failed.", e);
    }
  }

  public void setWithTtl(String key, String value, long ttlSeconds) {
    if (!available || connection == null) return;
    try {
      connection
          .async()
          .set(key, value, SetArgs.Builder.ex(ttlSeconds))
          .exceptionally(
              error -> {
                logger.log(Level.FINE, "[Redis] Set " + key + " failed.", error);
                return null;
              });
    } catch (Exception e) {
      logger.log(Level.FINE, "[Redis] Set " + key + " failed.", e);
    }
  }

  public List<String> scanValues(String pattern) {
    if (!available || connection == null) return List.of();
    try {
      var commands = connection.sync();
      ScanArgs args = ScanArgs.Builder.matches(pattern).limit(SCAN_BATCH);
      List<String> keys = new ArrayList<>();
      KeyScanCursor<String> cursor = commands.scan(args);
      keys.addAll(cursor.getKeys());
      while (!cursor.isFinished()) {
        cursor = commands.scan(cursor, args);
        keys.addAll(cursor.getKeys());
      }
      if (keys.isEmpty()) return List.of();
      return commands.mget(keys.toArray(new String[0])).stream()
          .map(kv -> kv.hasValue() ? kv.getValue() : null)
          .filter(v -> v != null)
          .toList();
    } catch (Exception e) {
      logger.log(Level.FINE, "[Redis] Scan " + pattern + " failed.", e);
      return List.of();
    }
  }

  public void subscribe(String channel, Consumer<String> onMessage) {
    if (pubSubConnection == null) return;
    pubSubConnection.addListener(
        new RedisPubSubAdapter<>() {
          @Override
          public void message(String receivedChannel, String message) {
            if (channel.equals(receivedChannel)) onMessage.accept(message);
          }
        });
    pubSubConnection.sync().subscribe(channel);
  }

  public void shutdown() {
    available = false;
    attempted = false;
    try {
      if (connection != null) connection.close();
    } catch (Exception e) {
      logger.log(Level.FINE, "[Redis] Error closing connection.", e);
    }
    try {
      if (pubSubConnection != null) pubSubConnection.close();
    } catch (Exception e) {
      logger.log(Level.FINE, "[Redis] Error closing pubSubConnection.", e);
    }
    try {
      if (client != null) client.shutdown(Duration.ZERO, SHUTDOWN_TIMEOUT);
    } catch (Exception e) {
      logger.log(Level.FINE, "[Redis] Error shutting down client.", e);
    }
    connection = null;
    pubSubConnection = null;
    client = null;
  }
}
