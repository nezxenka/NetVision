package club.nezxenka.netvision.service.command.impl.punish;

import club.nezxenka.netvision.audience.api.Sender;
import club.nezxenka.netvision.core.storage.connection.DatabaseManager;
import club.nezxenka.netvision.service.command.api.NetVisionCommand;
import club.nezxenka.netvision.util.message.Message;
import club.nezxenka.netvision.util.message.MessageUtil;
import org.bukkit.OfflinePlayer;
import org.incendo.cloud.CommandManager;
import org.incendo.cloud.bukkit.parser.OfflinePlayerParser;
import org.incendo.cloud.context.CommandContext;

public class PunishCommand implements NetVisionCommand {
  private final DatabaseManager databaseManager;

  public PunishCommand(DatabaseManager databaseManager) {
    this.databaseManager = databaseManager;
  }

  @Override
  public void register(CommandManager<Sender> manager, String rootName) {
    final var baseBuilder =
        manager.commandBuilder(rootName).literal("punish").permission("netvision.punish.manage");
    manager.command(
        baseBuilder
            .literal("reset")
            .required("target", OfflinePlayerParser.offlinePlayerParser())
            .handler(this::reset));
  }

  private void reset(CommandContext<Sender> context) {
    final Sender sender = context.sender();
    final OfflinePlayer target = context.get("target");
    databaseManager.getDatabase().resetAllViolationLevels(target.getUniqueId());
    MessageUtil.sendMessage(
        sender.getNativeSender(), Message.PUNISH_RESET_SUCCESS, "player", target.getName());
  }
}
