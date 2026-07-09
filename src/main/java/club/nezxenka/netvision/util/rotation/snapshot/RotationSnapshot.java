package club.nezxenka.netvision.util.rotation.snapshot;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RotationSnapshot {
  private float fromYaw;
  private float fromPitch;
  private float toYaw;
  private float toPitch;
  private float deltaYaw;
  private float deltaPitch;
}
