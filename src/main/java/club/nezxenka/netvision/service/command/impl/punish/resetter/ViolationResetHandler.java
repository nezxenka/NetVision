package club.nezxenka.netvision.service.command.impl.punish.resetter;

import club.nezxenka.netvision.core.storage.api.RecordStorage;
import java.util.UUID;

public class ViolationResetHandler {
  private final RecordStorage database;

  public ViolationResetHandler(RecordStorage database) {
    this.database = database;
  }

  public void resetAll(UUID playerUuid) {
    database.resetAllViolationLevels(playerUuid);
  }

  public void resetGroup(UUID playerUuid, String group) {
    database.resetViolationLevel(playerUuid, group);
  }
}
