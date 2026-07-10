package club.nezxenka.netvision.service.command.impl.brands;

import club.nezxenka.netvision.audience.api.Sender;
import club.nezxenka.netvision.service.command.api.NetVisionCommand;
import club.nezxenka.netvision.service.signal.internal.SignalManager;
import club.nezxenka.netvision.service.signal.model.SignalType;
import club.nezxenka.netvision.util.message.Message;
import club.nezxenka.netvision.util.message.MessageUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.incendo.cloud.CommandManager;
import org.incendo.cloud.context.CommandContext;

public class BrandsCommand implements NetVisionCommand {
  private final SignalManager alertManager;

  public BrandsCommand(SignalManager alertManager) {
    this.alertManager = alertManager;
  }

  @Override
  public void register(CommandManager<Sender> manager, String rootName) {
    manager.command(
        manager
            .commandBuilder(rootName)
            .literal("brands")
            .permission("netvision.brand")
            .handler(this::execute));
  }

  private void execute(CommandContext<Sender> context) {
    CommandSender nativeSender = context.sender().getNativeSender();
    if (nativeSender instanceof Player player) alertManager.toggle(player, SignalType.BRAND, false);
    else {
      alertManager.toggleConsoleAlerts(SignalType.BRAND);
      if (alertManager.isConsoleAlertsEnabled(SignalType.BRAND))
        MessageUtil.sendMessage(nativeSender, Message.BRAND_ALERTS_ENABLED);
      else MessageUtil.sendMessage(nativeSender, Message.BRAND_ALERTS_DISABLED);
    }
  }
}
