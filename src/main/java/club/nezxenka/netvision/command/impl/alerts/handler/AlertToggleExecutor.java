package club.nezxenka.netvision.command.impl.alerts.handler;

import club.nezxenka.netvision.alert.internal.AlertManager;
import club.nezxenka.netvision.alert.model.AlertType;
import club.nezxenka.netvision.util.message.Message;
import club.nezxenka.netvision.util.message.MessageUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AlertToggleExecutor {
  private final AlertManager alertManager;

  public AlertToggleExecutor(AlertManager alertManager) {
    this.alertManager = alertManager;
  }

  public void execute(CommandSender sender, AlertType type) {
    if (sender instanceof Player player) alertManager.toggle(player, type, false);
    else {
      alertManager.toggleConsoleAlerts(type);
      boolean enabled = alertManager.isConsoleAlertsEnabled(type);
      MessageUtil.sendMessage(sender, enabled ? Message.ALERTS_ENABLED : Message.ALERTS_DISABLED);
    }
  }
}
