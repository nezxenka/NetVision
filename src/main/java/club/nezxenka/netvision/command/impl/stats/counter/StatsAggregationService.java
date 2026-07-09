package club.nezxenka.netvision.command.impl.stats.counter;

import club.nezxenka.netvision.check.packet.ai.internal.AICheck;
import club.nezxenka.netvision.database.api.ViolationDatabase;
import club.nezxenka.netvision.player.manager.PlayerDataManager;
import java.util.concurrent.TimeUnit;

public class StatsAggregationService {
  private final ViolationDatabase database;
  private final PlayerDataManager playerDataManager;

  public StatsAggregationService(ViolationDatabase database, PlayerDataManager playerDataManager) {
    this.database = database;
    this.playerDataManager = playerDataManager;
  }

  public record AggregatedStats(
      int dailyFlags, int dailyViolators, int onlinePlayers, long suspiciousNow) {}

  public AggregatedStats aggregate() {
    long since = System.currentTimeMillis() - TimeUnit.DAYS.toMillis(1);
    int flags = database.getLogCount(since);
    int violators = database.getUniqueViolatorsSince(since);
    int online = org.bukkit.Bukkit.getOnlinePlayers().size();
    long suspicious =
        playerDataManager.getPlayers().stream()
            .filter(
                p -> {
                  AICheck c = p.getCheckManager().getCheck(AICheck.class);
                  return c != null && c.getBuffer() > 10;
                })
            .count();
    return new AggregatedStats(flags, violators, online, suspicious);
  }
}
