package club.nezxenka.netvision.check.registry.provider;

import club.nezxenka.netvision.NetVision;
import club.nezxenka.netvision.alert.internal.AlertManager;
import club.nezxenka.netvision.check.registry.CheckManager;
import club.nezxenka.netvision.config.core.ConfigManager;
import club.nezxenka.netvision.integration.worldguard.WorldGuardManager;
import club.nezxenka.netvision.player.model.NetVisionPlayer;
import club.nezxenka.netvision.server.provider.AIServerProvider;

public class CheckProviderFactory {
  public static CheckManager createForPlayer(
      NetVisionPlayer player,
      NetVision plugin,
      ConfigManager configManager,
      AIServerProvider aiServerProvider,
      WorldGuardManager worldGuardManager,
      AlertManager alertManager) {
    return new CheckManager(
        player, plugin, configManager, aiServerProvider, worldGuardManager, alertManager);
  }
}
