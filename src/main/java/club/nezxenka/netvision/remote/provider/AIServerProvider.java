package club.nezxenka.netvision.remote.provider;

import club.nezxenka.netvision.NetVision;
import club.nezxenka.netvision.core.config.ConfigManager;
import club.nezxenka.netvision.remote.connection.AIServer;
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
        plugin.getLogger().warning("[NeuralAnalyzer] AI is enabled but not configured.");
        this.currentInstance = null;
      } else {
        plugin.getLogger().info("[NeuralAnalyzer] AI Check loaded.");
        this.currentInstance = new AIServer(plugin, url, key);
      }
    } else {
      plugin.getLogger().info("[NeuralAnalyzer] AI Check disabled.");
      this.currentInstance = null;
    }
  }

  @Override
  public AIServer get() {
    return this.currentInstance;
  }
}
