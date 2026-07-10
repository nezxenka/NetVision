package club.nezxenka.netvision.service.enforce.internal.executor;

import club.nezxenka.netvision.NetVision;
import club.nezxenka.netvision.actor.model.NetVisionPlayer;
import club.nezxenka.netvision.core.storage.api.RecordStorage;
import club.nezxenka.netvision.engine.api.AnalysisModule;
import club.nezxenka.netvision.service.signal.internal.SignalManager;
import club.nezxenka.netvision.service.signal.model.SignalType;
import club.nezxenka.netvision.util.message.Message;
import club.nezxenka.netvision.util.message.MessageUtil;
import java.util.List;
import java.util.Locale;
import org.bukkit.Bukkit;

public class CommandExecutor {
  private final NetVisionPlayer player;
  private final NetVision plugin;
  private final RecordStorage database;
  private final SignalManager alertManager;

  public CommandExecutor(
      NetVisionPlayer player,
      NetVision plugin,
      RecordStorage database,
      SignalManager alertManager) {
    this.player = player;
    this.plugin = plugin;
    this.database = database;
    this.alertManager = alertManager;
  }

  public void execute(
      AnalysisModule module, int vl, String verbose, List<String> commands, String groupName) {
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
                            module.getModuleName(),
                            "vl",
                            String.valueOf(vl),
                            "verbose",
                            verbose),
                        SignalType.REGULAR));
      else if (lower.equals("[log]"))
        database.logAlert(player, verbose, module.getModuleName(), vl);
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
                                module.getModuleName(),
                                "vl",
                                String.valueOf(vl),
                                "verbose",
                                verbose)));
      } else {
        String formatted =
            cmd.replace("<player>", player.getPlayer().getName())
                .replace("<check_name>", module.getModuleName())
                .replace("<vl>", String.valueOf(vl))
                .replace("<verbose>", verbose);
        Bukkit.getScheduler()
            .runTask(plugin, () -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), formatted));
      }
    }
  }
}
