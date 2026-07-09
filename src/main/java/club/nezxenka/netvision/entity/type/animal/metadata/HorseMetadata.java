package club.nezxenka.netvision.entity.type.animal.metadata;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class HorseMetadata {
  private double jumpStrength;
  private double movementSpeed;
  private boolean tamed;
}
