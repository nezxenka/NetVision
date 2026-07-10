package club.nezxenka.netvision.entity.type.object;

import club.nezxenka.netvision.actor.model.NetVisionPlayer;
import club.nezxenka.netvision.entity.api.PacketEntity;
import com.github.retrooper.packetevents.protocol.entity.type.EntityType;
import java.util.UUID;

public class PacketEntityArmorStand extends PacketEntity {
  public PacketEntityArmorStand(NetVisionPlayer player, UUID uuid, EntityType type) {
    super(player, uuid, type);
  }
}
