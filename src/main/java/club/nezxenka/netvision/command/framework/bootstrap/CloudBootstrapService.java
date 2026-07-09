package club.nezxenka.netvision.command.framework.bootstrap;

import club.nezxenka.netvision.NetVision;
import club.nezxenka.netvision.sender.api.Sender;
import club.nezxenka.netvision.sender.factory.SenderFactory;
import org.incendo.cloud.bukkit.CloudBukkitCapabilities;
import org.incendo.cloud.execution.ExecutionCoordinator;
import org.incendo.cloud.paper.LegacyPaperCommandManager;

public class CloudBootstrapService {
  public LegacyPaperCommandManager<Sender> bootstrap(NetVision plugin) {
    SenderFactory senderFactory = new SenderFactory(plugin);
    LegacyPaperCommandManager<Sender> manager;
    try {
      manager =
          new LegacyPaperCommandManager<>(
              plugin, ExecutionCoordinator.simpleCoordinator(), senderFactory);
    } catch (Exception e) {
      plugin.getLogger().severe("Failed to initialize Cloud Command Manager: " + e.getMessage());
      return null;
    }
    if (manager.hasCapability(CloudBukkitCapabilities.NATIVE_BRIGADIER))
      manager.registerBrigadier();
    else if (manager.hasCapability(CloudBukkitCapabilities.ASYNCHRONOUS_COMPLETION))
      manager.registerAsynchronousCompletions();
    return manager;
  }
}
