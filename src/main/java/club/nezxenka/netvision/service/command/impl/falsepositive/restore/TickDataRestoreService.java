package club.nezxenka.netvision.service.command.impl.falsepositive.restore;

import club.nezxenka.netvision.engine.model.TickSample;
import club.nezxenka.netvision.engine.network.neural.NeuralAnalyzer;
import club.nezxenka.netvision.remote.restore.DataRestorer;
import java.util.List;

public class TickDataRestoreService {
  private final DataRestorer restorer;

  public TickDataRestoreService(DataRestorer restorer) {
    this.restorer = restorer;
  }

  public boolean restore(String playerName, NeuralAnalyzer check) {
    List<TickSample> history = check.getTickHistory();
    if (history == null || history.isEmpty()) return false;
    return restorer.restoreData(playerName, history);
  }
}
