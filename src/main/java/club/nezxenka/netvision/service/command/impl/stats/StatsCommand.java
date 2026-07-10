package club.nezxenka.netvision.service.command.impl.stats;

import club.nezxenka.netvision.NetVision;
import club.nezxenka.netvision.actor.manager.PlayerDataManager;
import club.nezxenka.netvision.audience.api.Sender;
import club.nezxenka.netvision.core.storage.api.RecordStorage;
import club.nezxenka.netvision.core.storage.connection.DatabaseManager;
import club.nezxenka.netvision.engine.network.neural.NeuralAnalyzer;
import club.nezxenka.netvision.service.command.api.NetVisionCommand;
import club.nezxenka.netvision.util.message.Message;
import club.nezxenka.netvision.util.message.MessageUtil;
import java.util.concurrent.TimeUnit;
import org.bukkit.Bukkit;
import org.incendo.cloud.CommandManager;
import org.incendo.cloud.context.CommandContext;

public class StatsCommand implements NetVisionCommand {
  private final NetVision plugin;
  private final DatabaseManager databaseManager;
  private final PlayerDataManager playerDataManager;

  public StatsCommand(
      NetVision plugin, DatabaseManager databaseManager, PlayerDataManager playerDataManager) {
    this.plugin = plugin;
    this.databaseManager = databaseManager;
    this.playerDataManager = playerDataManager;
  }

  @Override
  public void register(CommandManager<Sender> manager, String rootName) {
    manager.command(
        manager
            .commandBuilder(rootName)
            .literal("stats")
            .permission("netvision.stats")
            .handler(this::execute));
  }

  private void execute(CommandContext<Sender> context) {
    final Sender sender = context.sender();
    final RecordStorage db = databaseManager.getDatabase();
    if (db == null) {
      sender.sendMessage(MessageUtil.getMessage(Message.HISTORY_DISABLED));
      return;
    }
    Bukkit.getScheduler()
        .runTaskAsynchronously(
            plugin,
            () -> {
              long since = System.currentTimeMillis() - TimeUnit.DAYS.toMillis(1);
              int totalFlags = db.getLogCount(since);
              int uniqueViolators = db.getUniqueViolatorsSince(since);
              Bukkit.getScheduler()
                  .runTask(
                      plugin,
                      () ->
                          MessageUtil.sendMessageList(
                              sender.getNativeSender(),
                              Message.STATS_LINES,
                              "flags_24h",
                              String.valueOf(totalFlags),
                              "violators_24h",
                              String.valueOf(uniqueViolators),
                              "online_players",
                              String.valueOf(Bukkit.getOnlinePlayers().size()),
                              "suspicious_now",
                              String.valueOf(getSuspiciousCount())));
            });
  }

  private long getSuspiciousCount() {
    return playerDataManager.getPlayers().stream()
        .filter(
            sp -> {
              NeuralAnalyzer check = sp.getModuleCoordinator().getModule(NeuralAnalyzer.class);
              return check != null && check.getBuffer() > 10;
            })
        .count();
  }
}
