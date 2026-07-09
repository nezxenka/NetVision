package club.nezxenka.netvision.check.packet.ai.model.config;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AiModelParameters {
  private double cheatProbability;
  private double legitProbability;
  private double damageReductionProb;
  private double damageReductionMultiplier;
  private int maxTickHistory;
}
