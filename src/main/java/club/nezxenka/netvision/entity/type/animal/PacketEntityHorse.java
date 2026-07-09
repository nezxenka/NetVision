package club.nezxenka.netvision.entity.type.animal;

import club.nezxenka.netvision.entity.type.object.PacketEntityTrackXRot;
import club.nezxenka.netvision.player.model.NetVisionPlayer;
import com.github.retrooper.packetevents.protocol.entity.type.EntityType;
import java.util.UUID;

public class PacketEntityHorse extends PacketEntityTrackXRot {
  public PacketEntityHorse(NetVisionPlayer player, UUID uuid, EntityType type) {
    super(player, uuid, type);
  }
}
