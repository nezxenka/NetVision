package club.nezxenka.netvision.engine.network.neural.model.config;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ModelParameters {
  private double cheatProbability;
  private double legitProbability;
  private double damageReductionProb;
  private double damageReductionMultiplier;
  private int maxTickHistory;
}
