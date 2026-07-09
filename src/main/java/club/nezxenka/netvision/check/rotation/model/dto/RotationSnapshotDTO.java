package club.nezxenka.netvision.check.rotation.model.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RotationSnapshotDTO {
  private double sensitivityX;
  private double sensitivityY;
  private double divisorX;
  private double divisorY;
  private double modeX;
  private double modeY;
}
