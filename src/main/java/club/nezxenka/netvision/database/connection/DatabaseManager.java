package club.nezxenka.netvision.database.connection;

import club.nezxenka.netvision.NetVision;
import club.nezxenka.netvision.config.core.ConfigManager;
import club.nezxenka.netvision.database.api.ViolationDatabase;
import club.nezxenka.netvision.database.sqlite.SQLiteViolationDatabase;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.io.File;
import lombok.Getter;

@Getter
public class DatabaseManager {
  private final ViolationDatabase database;
  private final HikariDataSource dataSource;

  public DatabaseManager(NetVision plugin, ConfigManager configManager) {
    this.dataSource = createDataSource(plugin);
    this.database = new SQLiteViolationDatabase(this.dataSource, plugin, configManager);
  }

  private HikariDataSource createDataSource(NetVision plugin) {
    File dbFile = new File(plugin.getDataFolder(), "violations.db");
    HikariConfig config = new HikariConfig();
    config.setPoolName("NetVision-Pool");
    config.setDriverClassName("org.sqlite.JDBC");
    config.setJdbcUrl("jdbc:sqlite:" + dbFile.getAbsolutePath());
    int poolSize = Math.max(2, Math.min(8, Runtime.getRuntime().availableProcessors()));
    config.setMaximumPoolSize(poolSize);
    config.addDataSourceProperty("journal_mode", "WAL");
    config.addDataSourceProperty("synchronous", "NORMAL");
    config.addDataSourceProperty("busy_timeout", "5000");
    config.setConnectionTimeout(30000);
    config.setIdleTimeout(600000);
    config.setMaxLifetime(1800000);
    return new HikariDataSource(config);
  }

  public void shutdown() {
    if (dataSource != null && !dataSource.isClosed()) dataSource.close();
  }
}
