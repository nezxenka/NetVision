package club.nezxenka.netvision.command.impl.suspicious;

import club.nezxenka.netvision.alert.internal.AlertManager;
import club.nezxenka.netvision.alert.model.AlertType;
import club.nezxenka.netvision.check.packet.ai.internal.AICheck;
import club.nezxenka.netvision.command.api.NetVisionCommand;
import club.nezxenka.netvision.command.framework.CommandRegistrationService;
import club.nezxenka.netvision.command.requirement.PlayerSenderRequirement;
import club.nezxenka.netvision.player.manager.PlayerDataManager;
import club.nezxenka.netvision.player.model.NetVisionPlayer;
import club.nezxenka.netvision.sender.api.Sender;
import club.nezxenka.netvision.util.message.Message;
import club.nezxenka.netvision.util.message.MessageUtil;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.incendo.cloud.CommandManager;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.parser.standard.DoubleParser;

public class SuspiciousCommand implements NetVisionCommand {
  private final PlayerDataManager playerDataManager;
  private final AlertManager alertManager;

  public SuspiciousCommand(PlayerDataManager playerDataManager, AlertManager alertManager) {
    this.playerDataManager = playerDataManager;
    this.alertManager = alertManager;
  }

  @Override
  public void register(CommandManager<Sender> manager, String rootName) {
    final var base =
        manager.commandBuilder(rootName).literal("suspicious").permission("netvision.suspicious");
    manager.command(
        base.literal("alerts")
            .permission("netvision.suspicious.alerts")
            .apply(
                CommandRegistrationService.REQUIREMENT_FACTORY.create(
                    PlayerSenderRequirement.PLAYER_SENDER_REQUIREMENT))
            .handler(this::executeAlerts));
    manager.command(
        base.literal("list")
            .permission("netvision.suspicious.list")
            .flag(manager.flagBuilder("buffer").withComponent(DoubleParser.doubleParser(0.0)))
            .handler(this::executeList));
    manager.command(
        base.literal("top").permission("netvision.suspicious.top").handler(this::executeTop));
  }

  private void executeAlerts(CommandContext<Sender> context) {
    final Player player = context.sender().getPlayer();
    alertManager.toggle(player, AlertType.SUSPICIOUS, false);
  }

  private void executeList(CommandContext<Sender> context) {
    final Sender sender = context.sender();
    final Double bufferFlag = context.flags().get("buffer");
    final double bufferFilter = bufferFlag != null ? bufferFlag : 0.0;
    List<NetVisionPlayer> suspiciousPlayers =
        playerDataManager.getPlayers().stream()
            .filter(
                sp -> {
                  AICheck check = sp.getCheckManager().getCheck(AICheck.class);
                  return check != null && check.getBuffer() > bufferFilter;
                })
            .sorted(
                Comparator.comparingDouble(
                    sp -> -sp.getCheckManager().getCheck(AICheck.class).getBuffer()))
            .collect(Collectors.toList());
    if (suspiciousPlayers.isEmpty()) {
      sender.sendMessage(MessageUtil.getMessage(Message.SUSPICIOUS_LIST_EMPTY));
      return;
    }
    sender.sendMessage(
        MessageUtil.getMessage(
            Message.SUSPICIOUS_LIST_HEADER, "count", String.valueOf(suspiciousPlayers.size())));
    for (NetVisionPlayer sp : suspiciousPlayers) {
      AICheck aiCheck = sp.getCheckManager().getCheck(AICheck.class);
      double buffer = aiCheck.getBuffer();
      String playerName = sp.getPlayer().getName();
      Component entry =
          MessageUtil.getMessage(
              Message.SUSPICIOUS_LIST_ENTRY,
              "player",
              playerName,
              "buffer",
              String.format("%.1f", buffer),
              "ping",
              String.valueOf(sp.getPlayer().getPing()));
      sender.sendMessage(entry);
    }
  }

  private void executeTop(CommandContext<Sender> context) {
    final Sender sender = context.sender();
    NetVisionPlayer topPlayer =
        playerDataManager.getPlayers().stream()
            .filter(sp -> sp.getCheckManager().getCheck(AICheck.class) != null)
            .max(
                Comparator.comparingDouble(
                    sp -> sp.getCheckManager().getCheck(AICheck.class).getBuffer()))
            .orElse(null);
    if (topPlayer == null || topPlayer.getCheckManager().getCheck(AICheck.class).getBuffer() == 0) {
      sender.sendMessage(MessageUtil.getMessage(Message.SUSPICIOUS_TOP_NONE));
      return;
    }
    String playerName = topPlayer.getPlayer().getName();
    double buffer = topPlayer.getCheckManager().getCheck(AICheck.class).getBuffer();
    Component message =
        MessageUtil.getMessage(
            Message.SUSPICIOUS_TOP_PLAYER,
            "player",
            playerName,
            "buffer",
            String.format("%.1f", buffer));
    sender.sendMessage(message);
  }
}
