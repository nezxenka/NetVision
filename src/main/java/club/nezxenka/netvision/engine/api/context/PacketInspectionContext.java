package club.nezxenka.netvision.engine.api.context;

import club.nezxenka.netvision.actor.model.NetVisionPlayer;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PacketInspectionContext {
  private PacketReceiveEvent event;
  private NetVisionPlayer player;
  private boolean cancelled;
}
