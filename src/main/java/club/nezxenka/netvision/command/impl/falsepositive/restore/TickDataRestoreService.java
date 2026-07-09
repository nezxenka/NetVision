package club.nezxenka.netvision.command.impl.falsepositive.restore;

import club.nezxenka.netvision.check.packet.ai.internal.AICheck;
import club.nezxenka.netvision.data.TickData;
import club.nezxenka.netvision.server.restore.DataRestorer;
import java.util.List;

public class TickDataRestoreService {
  private final DataRestorer restorer;

  public TickDataRestoreService(DataRestorer restorer) {
    this.restorer = restorer;
  }

  public boolean restore(String playerName, AICheck check) {
    List<TickData> history = check.getTickHistory();
    if (history == null || history.isEmpty()) return false;
    return restorer.restoreData(playerName, history);
  }
}
