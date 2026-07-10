package club.nezxenka.netvision.service.command.impl.reload;

import club.nezxenka.netvision.NetVision;
import club.nezxenka.netvision.audience.api.Sender;
import club.nezxenka.netvision.service.command.api.NetVisionCommand;
import club.nezxenka.netvision.util.message.Message;
import club.nezxenka.netvision.util.message.MessageUtil;
import org.incendo.cloud.CommandManager;
import org.incendo.cloud.context.CommandContext;

public class ReloadCommand implements NetVisionCommand {
  private final NetVision plugin;

  public ReloadCommand(NetVision plugin) {
    this.plugin = plugin;
  }

  @Override
  public void register(CommandManager<Sender> manager, String rootName) {
    manager.command(
        manager
            .commandBuilder(rootName)
            .literal("reload")
            .permission("netvision.reload")
            .handler(this::execute));
  }

  private void execute(CommandContext<Sender> context) {
    MessageUtil.sendMessage(context.sender().getNativeSender(), Message.RELOAD_START);
    plugin.reloadPlugin();
    MessageUtil.sendMessage(context.sender().getNativeSender(), Message.RELOAD_SUCCESS);
  }
}
