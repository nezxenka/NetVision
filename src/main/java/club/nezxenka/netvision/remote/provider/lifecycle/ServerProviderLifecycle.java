package club.nezxenka.netvision.remote.provider.lifecycle;

import club.nezxenka.netvision.NetVision;
import club.nezxenka.netvision.core.config.ConfigManager;
import club.nezxenka.netvision.remote.connection.AIServer;
import java.util.logging.Logger;

public class ServerProviderLifecycle {
  private final NetVision plugin;
  private final ConfigManager configManager;
  private final Logger logger;

  public ServerProviderLifecycle(NetVision plugin, ConfigManager configManager, Logger logger) {
    this.plugin = plugin;
    this.configManager = configManager;
    this.logger = logger;
  }

  public AIServer createIfEnabled() {
    if (!configManager.isAiEnabled()) {
      logger.info("[NeuralAnalyzer] AI Check disabled.");
      return null;
    }
    String url = configManager.getAiServerUrl();
    String key = configManager.getAiApiKey();
    if (url == null || url.isEmpty() || key == null || key.equals("API-KEY")) {
      logger.warning("[NeuralAnalyzer] AI is enabled but not configured.");
      return null;
    }
    logger.info("[NeuralAnalyzer] AI Check loaded.");
    return new AIServer(plugin, url, key);
  }
}
