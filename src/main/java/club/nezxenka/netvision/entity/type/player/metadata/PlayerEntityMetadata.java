package club.nezxenka.netvision.entity.type.player.metadata;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PlayerEntityMetadata {
  private String name;
  private boolean sneaking;
  private boolean sprinting;
  private boolean invisible;
}
