package club.nezxenka.netvision.entity.type.player;

import club.nezxenka.netvision.entity.api.PacketEntity;
import club.nezxenka.netvision.player.model.NetVisionPlayer;
import com.github.retrooper.packetevents.protocol.entity.type.EntityTypes;
import java.util.UUID;

public class PacketEntityPlayer extends PacketEntity {
  public PacketEntityPlayer(NetVisionPlayer player, UUID uuid) {
    super(player, uuid, EntityTypes.PLAYER);
  }
}
