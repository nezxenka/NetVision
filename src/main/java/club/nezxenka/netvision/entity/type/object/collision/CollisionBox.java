package club.nezxenka.netvision.entity.type.object.collision;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CollisionBox {
  private double width;
  private double height;
  private double offsetX;
  private double offsetY;
  private double offsetZ;
}
