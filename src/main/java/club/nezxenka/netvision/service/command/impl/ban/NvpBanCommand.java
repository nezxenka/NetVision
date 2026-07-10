package club.nezxenka.netvision.service.command.impl.ban;

import club.nezxenka.netvision.NetVision;
import club.nezxenka.netvision.audience.api.Sender;
import club.nezxenka.netvision.service.command.api.NetVisionCommand;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.incendo.cloud.CommandManager;
import org.incendo.cloud.context.CommandContext;

public class NvpBanCommand implements NetVisionCommand {

  public NvpBanCommand(NetVision plugin) {}

  @Override
  public void register(CommandManager<Sender> manager, String rootName) {
    manager.command(
        manager
            .commandBuilder(rootName)
            .literal("ban")
            .permission("netvision.ban")
            .required("target", org.incendo.cloud.bukkit.parser.PlayerParser.playerParser())
            .handler(this::ban));
  }

  private void ban(CommandContext<Sender> context) {
    Player target = context.get("target");
    String targetName = target.getName();
    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "shame ban " + targetName);
  }
}
