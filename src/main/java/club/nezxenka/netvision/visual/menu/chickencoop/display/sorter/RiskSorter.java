package club.nezxenka.netvision.visual.menu.coop.display.sorter;

import club.nezxenka.netvision.visual.menu.coop.model.PlayerRiskData;
import java.util.Comparator;

public class RiskSorter implements Comparator<PlayerRiskData> {
  @Override
  public int compare(PlayerRiskData a, PlayerRiskData b) {
    return Double.compare(b.getAverageProbability(), a.getAverageProbability());
  }
}
