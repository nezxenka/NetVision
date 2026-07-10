package club.nezxenka.netvision.actor.manager.lifecycle;

import club.nezxenka.netvision.NetVision;
import club.nezxenka.netvision.service.signal.internal.SignalManager;
import club.nezxenka.netvision.service.signal.model.SignalType;
import org.bukkit.entity.Player;

public class PlayerLifecycleHandler {
  private final NetVision plugin;
  private final SignalManager alertManager;

  public PlayerLifecycleHandler(NetVision plugin, SignalManager alertManager) {
    this.plugin = plugin;
    this.alertManager = alertManager;
  }

  public void onJoinAutoEnable(Player player) {
    if (player.hasPermission("netvision.alerts")
        && player.hasPermission("netvision.alerts.enable-on-join")) {
      if (!alertManager.hasAlertsEnabled(player, SignalType.REGULAR))
        alertManager.toggle(player, SignalType.REGULAR, true);
    }
    if (player.hasPermission("netvision.brand")
        && player.hasPermission("netvision.brand.enable-on-join")) {
      if (!alertManager.hasAlertsEnabled(player, SignalType.BRAND))
        alertManager.toggle(player, SignalType.BRAND, true);
    }
  }

  public void onQuit(Player player) {
    plugin.getChickenCoopMenu().removePlayer(player.getUniqueId());
    plugin.getHologramManager().handlePlayerQuit(player);
    alertManager.handlePlayerQuit(player);
  }
}
