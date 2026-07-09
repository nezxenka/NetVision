package club.nezxenka.netvision.punishment.internal.executor;

import club.nezxenka.netvision.NetVision;
import club.nezxenka.netvision.alert.internal.AlertManager;
import club.nezxenka.netvision.alert.model.AlertType;
import club.nezxenka.netvision.check.api.Check;
import club.nezxenka.netvision.database.api.ViolationDatabase;
import club.nezxenka.netvision.player.model.NetVisionPlayer;
import club.nezxenka.netvision.util.message.Message;
import club.nezxenka.netvision.util.message.MessageUtil;
import java.util.List;
import java.util.Locale;
import org.bukkit.Bukkit;

public class CommandExecutor {
  private final NetVisionPlayer player;
  private final NetVision plugin;
  private final ViolationDatabase database;
  private final AlertManager alertManager;

  public CommandExecutor(
      NetVisionPlayer player,
      NetVision plugin,
      ViolationDatabase database,
      AlertManager alertManager) {
    this.player = player;
    this.plugin = plugin;
    this.database = database;
    this.alertManager = alertManager;
  }

  public void execute(
      Check check, int vl, String verbose, List<String> commands, String groupName) {
    for (String cmd : commands) {
      String lower = cmd.toLowerCase(Locale.ROOT);
      if (lower.equals("[alert]"))
        Bukkit.getScheduler()
            .runTask(
                plugin,
                () ->
                    alertManager.send(
                        MessageUtil.getMessage(
                            Message.ALERTS_FORMAT,
                            "player",
                            player.getPlayer().getName(),
                            "check_name",
                            check.getCheckName(),
                            "vl",
                            String.valueOf(vl),
                            "verbose",
                            verbose),
                        AlertType.REGULAR));
      else if (lower.equals("[log]")) database.logAlert(player, verbose, check.getCheckName(), vl);
      else if (lower.equals("[reset]")) database.resetViolationLevel(player.getUuid(), groupName);
      else if (lower.startsWith("[broadcast] ")) {
        String msg = cmd.substring("[broadcast] ".length());
        Bukkit.getScheduler()
            .runTask(
                plugin,
                () ->
                    plugin
                        .getAdventure()
                        .players()
                        .sendMessage(
                            MessageUtil.format(
                                msg,
                                "player",
                                player.getPlayer().getName(),
                                "check_name",
                                check.getCheckName(),
                                "vl",
                                String.valueOf(vl),
                                "verbose",
                                verbose)));
      } else {
        String formatted =
            cmd.replace("<player>", player.getPlayer().getName())
                .replace("<check_name>", check.getCheckName())
                .replace("<vl>", String.valueOf(vl))
                .replace("<verbose>", verbose);
        Bukkit.getScheduler()
            .runTask(plugin, () -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), formatted));
      }
    }
  }
}
