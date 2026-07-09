package club.nezxenka.netvision.entity.type.projectile.trajectory;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProjectileTrajectory {
  private double motionX;
  private double motionY;
  private double motionZ;
  private int lifetime;
}
