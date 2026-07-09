package club.nezxenka.netvision.debug.internal.logger;

import club.nezxenka.netvision.debug.model.DebugCategory;
import java.util.logging.Logger;

public class DebugLogger {
  private final Logger bukkitLogger;

  public DebugLogger(Logger bukkitLogger) {
    this.bukkitLogger = bukkitLogger;
  }

  public void log(DebugCategory category, String message, boolean enabled) {
    if (enabled) bukkitLogger.info("[DEBUG | " + category.name() + "] " + message);
  }
}
