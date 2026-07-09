package club.nezxenka.netvision.debug.internal;

import club.nezxenka.netvision.NetVision;
import club.nezxenka.netvision.config.core.ConfigManager;
import club.nezxenka.netvision.debug.model.DebugCategory;
import java.util.EnumSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class DebugManager {

  private final NetVision plugin;
  private final ConfigManager configManager;
  private final Set<DebugCategory> enabledCategories = EnumSet.noneOf(DebugCategory.class);

  public DebugManager(NetVision plugin, ConfigManager configManager) {
    this.plugin = plugin;
    this.configManager = configManager;
    reload();
  }

  public void reload() {
    enabledCategories.clear();
    List<String> enabledKeys = configManager.getEnabledDebugCategories();
    for (String key : enabledKeys) {
      try {
        enabledCategories.add(DebugCategory.valueOf(key.toUpperCase(Locale.ROOT)));
      } catch (IllegalArgumentException e) {
        plugin.getLogger().warning("Invalid debug category in config: " + key);
      }
    }
  }

  public boolean isEnabled(DebugCategory category) {
    return enabledCategories.contains(category);
  }

  public void log(DebugCategory category, String message) {
    if (isEnabled(category)) {
      plugin.getLogger().info("[DEBUG | " + category.name() + "] " + message);
    }
  }
}
