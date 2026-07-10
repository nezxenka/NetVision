package club.nezxenka.netvision.service.command.impl.alerts.handler;

import club.nezxenka.netvision.service.signal.internal.SignalManager;
import club.nezxenka.netvision.service.signal.model.SignalType;
import club.nezxenka.netvision.util.message.Message;
import club.nezxenka.netvision.util.message.MessageUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AlertToggleExecutor {
  private final SignalManager alertManager;

  public AlertToggleExecutor(SignalManager alertManager) {
    this.alertManager = alertManager;
  }

  public void execute(CommandSender sender, SignalType type) {
    if (sender instanceof Player player) alertManager.toggle(player, type, false);
    else {
      alertManager.toggleConsoleAlerts(type);
      boolean enabled = alertManager.isConsoleAlertsEnabled(type);
      MessageUtil.sendMessage(sender, enabled ? Message.ALERTS_ENABLED : Message.ALERTS_DISABLED);
    }
  }
}
