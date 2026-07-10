package club.nezxenka.netvision.actor.model.attribute;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PlayerAttributeSet {
  private double x;
  private double y;
  private double z;
  private float yaw;
  private float pitch;
  private float lastYaw;
  private float lastPitch;
  private String brand;
  private boolean bedrock;
}
