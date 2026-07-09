package club.nezxenka.netvision.command.framework;

import club.nezxenka.netvision.NetVision;
import club.nezxenka.netvision.alert.internal.AlertManager;
import club.nezxenka.netvision.config.core.ConfigManager;
import club.nezxenka.netvision.config.locale.LocaleManager;
import club.nezxenka.netvision.database.connection.DatabaseManager;
import club.nezxenka.netvision.menu.history.HistoryMenu;
import club.nezxenka.netvision.player.manager.PlayerDataManager;
import club.nezxenka.netvision.sender.api.Sender;
import club.nezxenka.netvision.sender.factory.SenderFactory;
import org.incendo.cloud.bukkit.CloudBukkitCapabilities;
import org.incendo.cloud.execution.ExecutionCoordinator;
import org.incendo.cloud.paper.LegacyPaperCommandManager;

public class CommandFramework {
  public CommandFramework(
      NetVision plugin,
      AlertManager alertManager,
      DatabaseManager databaseManager,
      ConfigManager configManager,
      LocaleManager localeManager,
      PlayerDataManager playerDataManager,
      HistoryMenu historyMenu) {
    LegacyPaperCommandManager<Sender> cloudManager = setupCloud(plugin);
    if (cloudManager != null)
      CommandRegistrationService.registerCommands(
          cloudManager,
          plugin,
          alertManager,
          databaseManager,
          configManager,
          localeManager,
          playerDataManager,
          historyMenu);
  }

  private LegacyPaperCommandManager<Sender> setupCloud(NetVision plugin) {
    SenderFactory senderFactory = new SenderFactory(plugin);
    LegacyPaperCommandManager<Sender> manager;
    try {
      manager =
          new LegacyPaperCommandManager<>(
              plugin, ExecutionCoordinator.simpleCoordinator(), senderFactory);
    } catch (Exception e) {
      plugin.getLogger().severe("Failed to initialize Cloud Command Manager: " + e.getMessage());
      e.printStackTrace();
      return null;
    }
    if (manager.hasCapability(CloudBukkitCapabilities.NATIVE_BRIGADIER))
      manager.registerBrigadier();
    else if (manager.hasCapability(CloudBukkitCapabilities.ASYNCHRONOUS_COMPLETION))
      manager.registerAsynchronousCompletions();
    return manager;
  }
}
