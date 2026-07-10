package club.nezxenka.netvision.engine.network.neural.model.probability;

public class ProbabilityEvaluator {
  public boolean isCheating(double probability, double threshold) {
    return probability > threshold;
  }

  public boolean isHighRisk(double probability) {
    return probability > 0.9;
  }

  public boolean isLowRisk(double probability) {
    return probability < 0.1;
  }

  public int updateProb90Counter(int current, double probability) {
    if (probability > 0.9) return current + 1;
    if (probability < 0.1) return Math.max(0, current - 1);
    return current;
  }
}
