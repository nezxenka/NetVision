package club.nezxenka.netvision.database.connection.pool;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ConnectionPoolConfig {
  private String poolName;
  private int maxPoolSize;
  private long connectionTimeout;
  private long idleTimeout;
  private long maxLifetime;
  private String journalMode;
  private String synchronous;
  private int busyTimeout;
}
