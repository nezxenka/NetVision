package club.nezxenka.netvision.service.command.impl.stats.counter;

import club.nezxenka.netvision.actor.manager.PlayerDataManager;
import club.nezxenka.netvision.core.storage.api.RecordStorage;
import club.nezxenka.netvision.engine.network.neural.NeuralAnalyzer;
import java.util.concurrent.TimeUnit;

public class StatsAggregationService {
  private final RecordStorage database;
  private final PlayerDataManager playerDataManager;

  public StatsAggregationService(RecordStorage database, PlayerDataManager playerDataManager) {
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
                  NeuralAnalyzer c = p.getModuleCoordinator().getModule(NeuralAnalyzer.class);
                  return c != null && c.getBuffer() > 10;
                })
            .count();
    return new AggregatedStats(flags, violators, online, suspicious);
  }
}
