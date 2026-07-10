package club.nezxenka.netvision.entity.type.player;

import club.nezxenka.netvision.actor.model.NetVisionPlayer;
import club.nezxenka.netvision.entity.api.PacketEntity;
import com.github.retrooper.packetevents.protocol.entity.type.EntityTypes;

public class PacketEntitySelf extends PacketEntity {
  public PacketEntitySelf(NetVisionPlayer player) {
    super(player, player.getUuid(), EntityTypes.PLAYER);
  }
}
