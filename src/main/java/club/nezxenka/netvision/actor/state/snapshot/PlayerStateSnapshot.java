package club.nezxenka.netvision.actor.state.snapshot;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PlayerStateSnapshot {
  private double posX;
  private double posY;
  private double posZ;
  private float yaw;
  private float pitch;
  private boolean onGround;
  private long timestamp;
}
