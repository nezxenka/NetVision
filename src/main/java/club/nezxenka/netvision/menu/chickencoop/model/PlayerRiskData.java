package club.nezxenka.netvision.menu.chickencoop.model;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.List;
import java.util.UUID;

public class PlayerRiskData {

  private final UUID uuid;
  private final String playerName;
  private final Deque<Double> probabilities = new ArrayDeque<>();
  private static final int MAX_PROBABILITIES = 10;

  public PlayerRiskData(UUID uuid, String playerName) {
    this.uuid = uuid;
    this.playerName = playerName;
  }

  public void addProbability(double probability) {
    probabilities.addLast(probability);
    if (probabilities.size() > MAX_PROBABILITIES) probabilities.removeFirst();
  }

  public List<Double> getLastProbabilities() {
    List<Double> reversed = new ArrayList<>(probabilities);
    Collections.reverse(reversed);
    return reversed;
  }

  public double getAverageProbability() {
    if (probabilities.isEmpty()) return 0.0;
    return probabilities.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
  }

  public UUID getUuid() {
    return uuid;
  }

  public String getPlayerName() {
    return playerName;
  }
}
