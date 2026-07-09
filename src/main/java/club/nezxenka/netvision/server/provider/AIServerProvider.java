package club.nezxenka.netvision.server.provider;

import club.nezxenka.netvision.NetVision;
import club.nezxenka.netvision.config.core.ConfigManager;
import club.nezxenka.netvision.server.connection.AIServer;
import java.util.function.Supplier;

public class AIServerProvider implements Supplier<AIServer> {
  private final NetVision plugin;
  private final ConfigManager configManager;
  private AIServer currentInstance;

  public AIServerProvider(NetVision plugin, ConfigManager configManager) {
    this.plugin = plugin;
    this.configManager = configManager;
    this.reload();
  }

  public void reload() {
    if (configManager.isAiEnabled()) {
      String url = configManager.getAiServerUrl();
      String key = configManager.getAiApiKey();
      if (url == null || url.isEmpty() || key == null || key.equals("API-KEY")) {
        plugin.getLogger().warning("[AICheck] AI is enabled but not configured.");
        this.currentInstance = null;
      } else {
        plugin.getLogger().info("[AICheck] AI Check loaded.");
        this.currentInstance = new AIServer(plugin, url, key);
      }
    } else {
      plugin.getLogger().info("[AICheck] AI Check disabled.");
      this.currentInstance = null;
    }
  }

  @Override
  public AIServer get() {
    return this.currentInstance;
  }
}
