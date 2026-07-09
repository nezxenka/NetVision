package club.nezxenka.netvision.entity.type.projectile;

import club.nezxenka.netvision.entity.api.PacketEntity;
import club.nezxenka.netvision.player.model.NetVisionPlayer;
import com.github.retrooper.packetevents.protocol.entity.type.EntityType;
import java.util.UUID;

public class PacketEntityUnHittable extends PacketEntity {
  public PacketEntityUnHittable(NetVisionPlayer player, UUID uuid, EntityType type) {
    super(player, uuid, type);
  }
}
