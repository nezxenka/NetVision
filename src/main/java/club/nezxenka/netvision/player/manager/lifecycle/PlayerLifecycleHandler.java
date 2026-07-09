package club.nezxenka.netvision.player.manager.lifecycle;

import club.nezxenka.netvision.NetVision;
import club.nezxenka.netvision.alert.internal.AlertManager;
import club.nezxenka.netvision.alert.model.AlertType;
import org.bukkit.entity.Player;

public class PlayerLifecycleHandler {
  private final NetVision plugin;
  private final AlertManager alertManager;

  public PlayerLifecycleHandler(NetVision plugin, AlertManager alertManager) {
    this.plugin = plugin;
    this.alertManager = alertManager;
  }

  public void onJoinAutoEnable(Player player) {
    if (player.hasPermission("netvision.alerts")
        && player.hasPermission("netvision.alerts.enable-on-join")) {
      if (!alertManager.hasAlertsEnabled(player, AlertType.REGULAR))
        alertManager.toggle(player, AlertType.REGULAR, true);
    }
    if (player.hasPermission("netvision.brand")
        && player.hasPermission("netvision.brand.enable-on-join")) {
      if (!alertManager.hasAlertsEnabled(player, AlertType.BRAND))
        alertManager.toggle(player, AlertType.BRAND, true);
    }
  }

  public void onQuit(Player player) {
    plugin.getChickenCoopMenu().removePlayer(player.getUniqueId());
    plugin.getHologramManager().handlePlayerQuit(player);
    alertManager.handlePlayerQuit(player);
  }
}
