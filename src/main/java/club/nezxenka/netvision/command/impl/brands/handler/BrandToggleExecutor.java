package club.nezxenka.netvision.command.impl.brands.handler;

import club.nezxenka.netvision.alert.internal.AlertManager;
import club.nezxenka.netvision.alert.model.AlertType;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class BrandToggleExecutor {
  private final AlertManager alertManager;

  public BrandToggleExecutor(AlertManager alertManager) {
    this.alertManager = alertManager;
  }

  public void toggle(CommandSender sender) {
    if (sender instanceof Player player) alertManager.toggle(player, AlertType.BRAND, false);
    else alertManager.toggleConsoleAlerts(AlertType.BRAND);
  }
}
