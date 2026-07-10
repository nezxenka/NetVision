package club.nezxenka.netvision.engine.coordinator;

import club.nezxenka.netvision.NetVision;
import club.nezxenka.netvision.actor.model.NetVisionPlayer;
import club.nezxenka.netvision.core.config.ConfigManager;
import club.nezxenka.netvision.integration.worldguard.WorldGuardManager;
import club.nezxenka.netvision.remote.provider.AIServerProvider;
import club.nezxenka.netvision.service.signal.internal.SignalManager;

public class ModuleProvider {
  public static ModuleCoordinator createForPlayer(
      NetVisionPlayer player,
      NetVision plugin,
      ConfigManager configManager,
      AIServerProvider aiServerProvider,
      WorldGuardManager worldGuardManager,
      SignalManager alertManager) {
    return new ModuleCoordinator(
        player, plugin, configManager, aiServerProvider, worldGuardManager, alertManager);
  }
}
