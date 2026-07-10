package club.nezxenka.netvision.service.enforce.model.threshold;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ThresholdConfig {
  private int violationLevel;
  private String action;
  private int delay;
}
