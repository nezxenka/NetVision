package club.nezxenka.netvision.core.config.section;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AiConfigSection {
  private boolean enabled;
  private String serverUrl;
  private String apiKey;
  private int sequence;
  private int step;
  private double flag;
  private double resetOnFlag;
  private double bufferMultiplier;
  private double bufferDecrease;
}
