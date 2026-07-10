package club.nezxenka.netvision.actor.model.network;

import com.github.retrooper.packetevents.protocol.player.ClientVersion;
import com.github.retrooper.packetevents.protocol.player.GameMode;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class NetworkProfile {
  private ClientVersion clientVersion;
  private GameMode gameMode;
  private int entityId;
  private long joinTime;
}
