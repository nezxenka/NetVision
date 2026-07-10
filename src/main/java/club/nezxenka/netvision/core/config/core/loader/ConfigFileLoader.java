package club.nezxenka.netvision.core.config.loader;

import java.io.File;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class ConfigFileLoader {
  private final JavaPlugin plugin;

  public ConfigFileLoader(JavaPlugin plugin) {
    this.plugin = plugin;
  }

  public FileConfiguration loadOrCreate(String resourceName, String fileName) {
    plugin.saveDefaultConfig();
    plugin.reloadConfig();
    File file = new File(plugin.getDataFolder(), fileName);
    if (!file.exists()) plugin.saveResource(resourceName, false);
    return YamlConfiguration.loadConfiguration(file);
  }
}
