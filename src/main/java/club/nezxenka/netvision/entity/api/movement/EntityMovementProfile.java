package club.nezxenka.netvision.entity.api.movement;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EntityMovementProfile {
  private double posX;
  private double posY;
  private double posZ;
  private float yaw;
  private float pitch;
  private boolean onGround;
}
