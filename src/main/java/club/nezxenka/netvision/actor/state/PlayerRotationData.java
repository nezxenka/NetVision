package club.nezxenka.netvision.actor.state;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class PlayerRotationData {
  private final float yaw;
  private final float pitch;
  private final int transactionId;
}
