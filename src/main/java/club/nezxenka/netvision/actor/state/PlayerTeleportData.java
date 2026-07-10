package club.nezxenka.netvision.actor.state;

import com.github.retrooper.packetevents.protocol.teleport.RelativeFlag;
import com.github.retrooper.packetevents.util.Vector3d;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class PlayerTeleportData {
  private final Vector3d location;
  private final RelativeFlag flags;
  private final int transactionId;

  public boolean isRelativeX() {
    return flags.has(RelativeFlag.X);
  }

  public boolean isRelativeY() {
    return flags.has(RelativeFlag.Y);
  }

  public boolean isRelativeZ() {
    return flags.has(RelativeFlag.Z);
  }
}
