package club.nezxenka.netvision.database.api;

import club.nezxenka.netvision.database.model.PlayerMenuData;
import club.nezxenka.netvision.database.model.ProbabilityEntry;
import club.nezxenka.netvision.database.model.Violation;
import club.nezxenka.netvision.player.model.NetVisionPlayer;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface ViolationDatabase {
  void logAlert(NetVisionPlayer player, String verbose, String checkName, int vls);

  int getLogCount(UUID player);

  List<Violation> getViolations(UUID player, int page, int limit);

  int getUniqueViolatorsSince(long since);

  int getLogCount(long since);

  List<Violation> getViolations(int page, int limit, long since);

  int getViolationLevel(UUID playerUUID, String punishGroupName);

  int incrementViolationLevel(UUID playerUUID, String punishGroupName);

  void resetViolationLevel(UUID playerUUID, String punishGroupName);

  void resetAllViolationLevels(UUID playerUUID);

  void saveProbability(UUID uuid, String playerName, double probability);

  List<Double> getPlayerProbabilities(UUID uuid, int limit);

  List<ProbabilityEntry> getPlayerProbabilityEntries(UUID uuid, int limit, int offset);

  int getPlayerProbabilityCount(UUID uuid);

  void deletePlayerProbabilities(UUID uuid);

  Map<UUID, PlayerMenuData> getAllOnlinePlayerMenuData();
}
