package club.nezxenka.netvision.command.impl.logs;

import club.nezxenka.netvision.NetVision;
import club.nezxenka.netvision.command.api.NetVisionCommand;
import club.nezxenka.netvision.config.core.ConfigManager;
import club.nezxenka.netvision.config.locale.LocaleManager;
import club.nezxenka.netvision.database.connection.DatabaseManager;
import club.nezxenka.netvision.database.model.Violation;
import club.nezxenka.netvision.sender.api.Sender;
import club.nezxenka.netvision.util.message.Message;
import club.nezxenka.netvision.util.message.MessageUtil;
import club.nezxenka.netvision.util.time.TimeUtil;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.incendo.cloud.CommandManager;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.parser.standard.IntegerParser;
import org.incendo.cloud.parser.standard.StringParser;

public class LogsCommand implements NetVisionCommand {

  private final NetVision plugin;
  private final DatabaseManager databaseManager;
  private final ConfigManager configManager;
  private final LocaleManager localeManager;

  public LogsCommand(
      NetVision plugin,
      DatabaseManager databaseManager,
      ConfigManager configManager,
      LocaleManager localeManager) {
    this.plugin = plugin;
    this.databaseManager = databaseManager;
    this.configManager = configManager;
    this.localeManager = localeManager;
  }

  @Override
  public void register(CommandManager<Sender> manager, String rootName) {
    manager.command(
        manager
            .commandBuilder(rootName)
            .literal("logs")
            .permission("netvision.logs")
            .optional("page", IntegerParser.integerParser(1))
            .flag(manager.flagBuilder("time").withComponent(StringParser.stringParser()))
            .handler(this::handleLogs));
  }

  private void handleLogs(CommandContext<Sender> context) {
    Sender sender = context.sender();
    int page = context.getOrDefault("page", 1);
    String timeArg = context.flags().get("time");
    if (databaseManager.getDatabase() == null
        || !configManager.getConfig().getBoolean("history.enabled", false)) {
      MessageUtil.sendMessage(sender.getNativeSender(), Message.HISTORY_DISABLED);
      return;
    }
    long since = parseTime(timeArg);
    if (since == -1L) {
      MessageUtil.sendMessage(sender.getNativeSender(), Message.LOGS_INVALID_TIME);
      return;
    }
    plugin
        .getServer()
        .getScheduler()
        .runTaskAsynchronously(
            plugin,
            () -> {
              int entriesPerPage = 10;
              List<Violation> violations =
                  databaseManager.getDatabase().getViolations(page, entriesPerPage, since);
              int totalLogs = databaseManager.getDatabase().getLogCount(since);
              int maxPages = Math.max(1, (int) Math.ceil((double) totalLogs / entriesPerPage));
              MessageUtil.sendMessage(
                  sender.getNativeSender(),
                  Message.LOGS_HEADER,
                  "page",
                  String.valueOf(page),
                  "max_pages",
                  String.valueOf(maxPages));
              if (violations.isEmpty()) {
                MessageUtil.sendMessage(sender.getNativeSender(), Message.LOGS_NO_VIOLATIONS);
                return;
              }
              for (Violation violation : violations)
                sender.sendMessage(
                    MessageUtil.getMessage(
                        Message.LOGS_ENTRY,
                        "server",
                        violation.serverName(),
                        "player",
                        violation.playerName(),
                        "check",
                        violation.checkName(),
                        "vl",
                        String.valueOf(violation.vl()),
                        "verbose",
                        violation.verbose(),
                        "timeago",
                        TimeUtil.formatTimeAgo(violation.createdAt(), localeManager)));
            });
  }

  private long parseTime(String timeArg) {
    if (timeArg == null) return 0L;
    try {
      if (timeArg.length() < 2) return -1L;
      long value = Long.parseLong(timeArg.substring(0, timeArg.length() - 1));
      char unit = Character.toLowerCase(timeArg.charAt(timeArg.length() - 1));
      long multiplier =
          switch (unit) {
            case 'm' -> TimeUnit.MINUTES.toMillis(1);
            case 'h' -> TimeUnit.HOURS.toMillis(1);
            case 'd' -> TimeUnit.DAYS.toMillis(1);
            default -> -1L;
          };
      if (multiplier == -1L) return -1L;
      return System.currentTimeMillis() - value * multiplier;
    } catch (NumberFormatException e) {
      return -1L;
    }
  }
}
