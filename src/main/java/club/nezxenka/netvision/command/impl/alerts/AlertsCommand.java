package club.nezxenka.netvision.command.impl.alerts;

import club.nezxenka.netvision.alert.internal.AlertManager;
import club.nezxenka.netvision.alert.model.AlertType;
import club.nezxenka.netvision.command.api.NetVisionCommand;
import club.nezxenka.netvision.sender.api.Sender;
import club.nezxenka.netvision.util.message.Message;
import club.nezxenka.netvision.util.message.MessageUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.incendo.cloud.CommandManager;
import org.incendo.cloud.context.CommandContext;

public class AlertsCommand implements NetVisionCommand {
  private final AlertManager alertManager;

  public AlertsCommand(AlertManager alertManager) {
    this.alertManager = alertManager;
  }

  @Override
  public void register(CommandManager<Sender> manager, String rootName) {
    manager.command(
        manager
            .commandBuilder(rootName)
            .literal("alerts")
            .permission("netvision.alerts")
            .handler(this::execute));
  }

  private void execute(CommandContext<Sender> context) {
    CommandSender nativeSender = context.sender().getNativeSender();
    if (nativeSender instanceof Player player)
      alertManager.toggle(player, AlertType.REGULAR, false);
    else {
      alertManager.toggleConsoleAlerts(AlertType.REGULAR);
      if (alertManager.isConsoleAlertsEnabled(AlertType.REGULAR))
        MessageUtil.sendMessage(nativeSender, Message.ALERTS_ENABLED);
      else MessageUtil.sendMessage(nativeSender, Message.ALERTS_DISABLED);
    }
  }
}
