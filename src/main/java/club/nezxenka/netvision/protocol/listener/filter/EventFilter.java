package club.nezxenka.netvision.protocol.listener.filter;

import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import org.bukkit.entity.Player;

public class EventFilter {
  public boolean isPlayerEvent(PacketReceiveEvent event) {
    return event.getPlayer() instanceof Player;
  }

  public boolean isPlayServerPacket(Object packetType) {
    return packetType
        instanceof com.github.retrooper.packetevents.protocol.packettype.PacketType.Play.Server;
  }
}
