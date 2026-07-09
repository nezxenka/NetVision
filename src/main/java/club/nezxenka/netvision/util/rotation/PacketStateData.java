package club.nezxenka.netvision.util.rotation;

import com.github.retrooper.packetevents.util.Vector3d;
import lombok.Getter;

@Getter
public class PacketStateData {
  public boolean packetPlayerOnGround = false;
  public boolean lastPacketWasTeleport = false;
  public boolean lastPacketWasServerRotation = false;
  public boolean lastPacketWasOnePointSeventeenDuplicate = false;
  public boolean ignoreDuplicatePacketRotation = true;
  public Vector3d lastClaimedPosition = new Vector3d(0, 0, 0);
}
