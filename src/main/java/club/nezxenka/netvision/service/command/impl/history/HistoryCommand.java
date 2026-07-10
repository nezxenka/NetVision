package club.nezxenka.netvision.service.command.impl.history;

import club.nezxenka.netvision.audience.api.Sender;
import club.nezxenka.netvision.service.command.api.NetVisionCommand;
import club.nezxenka.netvision.service.command.framework.CommandRegistrationService;
import club.nezxenka.netvision.service.command.requirement.PlayerSenderRequirement;
import club.nezxenka.netvision.visual.menu.history.HistoryMenu;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.incendo.cloud.CommandManager;
import org.incendo.cloud.bukkit.parser.OfflinePlayerParser;
import org.incendo.cloud.context.CommandContext;

public class HistoryCommand implements NetVisionCommand {
  private final HistoryMenu historyMenu;

  public HistoryCommand(HistoryMenu historyMenu) {
    this.historyMenu = historyMenu;
  }

  @Override
  public void register(CommandManager<Sender> manager, String rootName) {
    manager.command(
        manager
            .commandBuilder(rootName)
            .literal("history", "hist")
            .permission("netvision.history")
            .required("target", OfflinePlayerParser.offlinePlayerParser())
            .apply(
                CommandRegistrationService.REQUIREMENT_FACTORY.create(
                    PlayerSenderRequirement.PLAYER_SENDER_REQUIREMENT))
            .handler(this::handleHistory));
  }

  private void handleHistory(CommandContext<Sender> context) {
    Player viewer = context.sender().getPlayer();
    OfflinePlayer target = context.get("target");
    historyMenu.open(viewer, target.getName(), target.getUniqueId(), 1);
  }
}
