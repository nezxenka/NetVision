package club.nezxenka.netvision.core.storage.api;

import club.nezxenka.netvision.actor.model.NetVisionPlayer;
import club.nezxenka.netvision.core.storage.model.Infraction;
import club.nezxenka.netvision.core.storage.model.PlayerMenuData;
import club.nezxenka.netvision.core.storage.model.ProbabilityEntry;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface RecordStorage {
  void logAlert(NetVisionPlayer player, String verbose, String moduleName, int vls);

  int getLogCount(UUID player);

  List<Infraction> getViolations(UUID player, int page, int limit);

  int getUniqueViolatorsSince(long since);

  int getLogCount(long since);

  List<Infraction> getViolations(int page, int limit, long since);

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
