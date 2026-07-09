package club.nezxenka.netvision.hologram.model;

import java.util.ArrayList;
import java.util.List;

public class ProbabilityHistory {
  private final Double[] probabilities = new Double[5];
  private int currentIndex = 0;
  private int count = 0;

  public void add(double probability) {
    probabilities[currentIndex] = probability;
    currentIndex = (currentIndex + 1) % 5;
    if (count < 5) count++;
  }

  public List<Double> getAll() {
    List<Double> result = new ArrayList<>();
    if (count == 0) return result;
    for (int i = 0; i < count; i++) {
      int index = (currentIndex - 1 - i + 50) % 5;
      if (probabilities[index] != null) result.add(probabilities[index]);
    }
    return result;
  }

  public double getAverage() {
    if (count == 0) return 0.0;
    double sum = 0.0;
    for (int i = 0; i < count; i++) {
      if (probabilities[i] != null) sum += probabilities[i];
    }
    return sum / count;
  }

  public boolean isEmpty() {
    return count == 0;
  }
}
