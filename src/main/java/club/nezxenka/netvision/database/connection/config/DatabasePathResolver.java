package club.nezxenka.netvision.database.connection.config;

import java.io.File;
import org.bukkit.plugin.java.JavaPlugin;

public class DatabasePathResolver {
  private final JavaPlugin plugin;

  public DatabasePathResolver(JavaPlugin plugin) {
    this.plugin = plugin;
  }

  public File resolve(String fileName) {
    return new File(plugin.getDataFolder(), fileName);
  }

  public String jdbcUrl(File dbFile) {
    return "jdbc:sqlite:" + dbFile.getAbsolutePath();
  }
}
