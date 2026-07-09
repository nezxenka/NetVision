package club.nezxenka.netvision.command.impl.punish.resetter;

import club.nezxenka.netvision.database.api.ViolationDatabase;
import java.util.UUID;

public class ViolationResetHandler {
  private final ViolationDatabase database;

  public ViolationResetHandler(ViolationDatabase database) {
    this.database = database;
  }

  public void resetAll(UUID playerUuid) {
    database.resetAllViolationLevels(playerUuid);
  }

  public void resetGroup(UUID playerUuid, String group) {
    database.resetViolationLevel(playerUuid, group);
  }
}
