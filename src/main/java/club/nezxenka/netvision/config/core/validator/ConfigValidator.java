package club.nezxenka.netvision.config.core.validator;

import java.util.logging.Logger;

public class ConfigValidator {
  private final Logger logger;

  public ConfigValidator(Logger logger) {
    this.logger = logger;
  }

  public boolean validateAiConfig(String url, String apiKey) {
    if (url == null || url.isEmpty()) {
      logger.warning("AI server URL is empty");
      return false;
    }
    if (apiKey == null || apiKey.equals("API-KEY")) {
      logger.warning("AI API key is not configured");
      return false;
    }
    return true;
  }

  public int clampPoolSize(int requested) {
    return Math.max(2, Math.min(8, requested));
  }
}
