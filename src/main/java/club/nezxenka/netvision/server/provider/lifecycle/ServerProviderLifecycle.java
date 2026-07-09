package club.nezxenka.netvision.server.provider.lifecycle;

import club.nezxenka.netvision.NetVision;
import club.nezxenka.netvision.config.core.ConfigManager;
import club.nezxenka.netvision.server.connection.AIServer;
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
      logger.info("[AICheck] AI Check disabled.");
      return null;
    }
    String url = configManager.getAiServerUrl();
    String key = configManager.getAiApiKey();
    if (url == null || url.isEmpty() || key == null || key.equals("API-KEY")) {
      logger.warning("[AICheck] AI is enabled but not configured.");
      return null;
    }
    logger.info("[AICheck] AI Check loaded.");
    return new AIServer(plugin, url, key);
  }
}
