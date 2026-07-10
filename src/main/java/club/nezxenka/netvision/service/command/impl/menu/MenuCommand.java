package club.nezxenka.netvision.service.command.impl.menu;

import club.nezxenka.netvision.audience.api.Sender;
import club.nezxenka.netvision.service.command.api.NetVisionCommand;
import club.nezxenka.netvision.util.message.Message;
import club.nezxenka.netvision.util.message.MessageUtil;
import club.nezxenka.netvision.visual.menu.coop.ChickenCoopMenu;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.incendo.cloud.CommandManager;
import org.incendo.cloud.context.CommandContext;

public class MenuCommand implements NetVisionCommand {
  private final ChickenCoopMenu chickenCoopMenu;

  public MenuCommand(ChickenCoopMenu chickenCoopMenu) {
    this.chickenCoopMenu = chickenCoopMenu;
  }

  @Override
  public void register(CommandManager<Sender> manager, String rootName) {
    manager.command(
        manager
            .commandBuilder(rootName)
            .literal("menu")
            .permission("netvision.menu")
            .handler(this::execute));
  }

  private void execute(CommandContext<Sender> context) {
    CommandSender sender = context.sender().getNativeSender();
    if (!(sender instanceof Player player)) {
      MessageUtil.sendMessage(sender, Message.RUN_AS_PLAYER);
      return;
    }
    chickenCoopMenu.openMenu(player);
  }
}
