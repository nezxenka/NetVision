package club.nezxenka.netvision.command.impl.help;

import club.nezxenka.netvision.command.api.NetVisionCommand;
import club.nezxenka.netvision.sender.api.Sender;
import club.nezxenka.netvision.util.message.Message;
import club.nezxenka.netvision.util.message.MessageUtil;
import org.incendo.cloud.CommandManager;
import org.incendo.cloud.context.CommandContext;

public class HelpCommand implements NetVisionCommand {
  @Override
  public void register(CommandManager<Sender> manager, String rootName) {
    final var builder = manager.commandBuilder(rootName).permission("netvision.help");
    manager.command(builder.handler(this::help));
    manager.command(builder.literal("help").handler(this::help));
  }

  private void help(CommandContext<Sender> context) {
    final Sender sender = context.sender();
    MessageUtil.sendMessageList(sender.getNativeSender(), Message.HELP_MESSAGE);
  }
}
