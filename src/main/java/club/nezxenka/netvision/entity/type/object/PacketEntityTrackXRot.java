package club.nezxenka.netvision.entity.type.object;

import club.nezxenka.netvision.entity.api.PacketEntity;
import club.nezxenka.netvision.player.model.NetVisionPlayer;
import com.github.retrooper.packetevents.protocol.entity.type.EntityType;
import java.util.UUID;

public class PacketEntityTrackXRot extends PacketEntity {
  public PacketEntityTrackXRot(NetVisionPlayer player, UUID uuid, EntityType type) {
    super(player, uuid, type);
  }
}
