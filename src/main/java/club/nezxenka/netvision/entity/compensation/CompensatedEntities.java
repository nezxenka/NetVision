package club.nezxenka.netvision.entity.compensation;

import club.nezxenka.netvision.entity.api.PacketEntity;
import club.nezxenka.netvision.entity.type.animal.PacketEntityHorse;
import club.nezxenka.netvision.entity.type.object.PacketEntityArmorStand;
import club.nezxenka.netvision.entity.type.object.PacketEntityTrackXRot;
import club.nezxenka.netvision.entity.type.player.PacketEntityPlayer;
import club.nezxenka.netvision.entity.type.player.PacketEntitySelf;
import club.nezxenka.netvision.entity.type.projectile.PacketEntityUnHittable;
import club.nezxenka.netvision.player.model.NetVisionPlayer;
import com.github.retrooper.packetevents.protocol.entity.type.EntityType;
import com.github.retrooper.packetevents.protocol.entity.type.EntityTypes;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import java.util.UUID;

public class CompensatedEntities {
  private final NetVisionPlayer player;
  public final Int2ObjectOpenHashMap<PacketEntity> entityMap = new Int2ObjectOpenHashMap<>();
  public final PacketEntitySelf self;

  public CompensatedEntities(NetVisionPlayer player) {
    this.player = player;
    this.self = new PacketEntitySelf(player);
  }

  public void addEntity(int entityId, UUID uuid, EntityType type) {
    PacketEntity packetEntity;
    if (type == EntityTypes.PLAYER) packetEntity = new PacketEntityPlayer(player, uuid);
    else if (EntityTypes.isTypeInstanceOf(type, EntityTypes.ABSTRACT_HORSE))
      packetEntity = new PacketEntityHorse(player, uuid, type);
    else if (EntityTypes.isTypeInstanceOf(type, EntityTypes.BOAT) || type == EntityTypes.CHICKEN)
      packetEntity = new PacketEntityTrackXRot(player, uuid, type);
    else if (EntityTypes.isTypeInstanceOf(type, EntityTypes.ABSTRACT_ARROW)
        || type == EntityTypes.FIREWORK_ROCKET
        || type == EntityTypes.ITEM) packetEntity = new PacketEntityUnHittable(player, uuid, type);
    else if (type == EntityTypes.ARMOR_STAND)
      packetEntity = new PacketEntityArmorStand(player, uuid, type);
    else packetEntity = new PacketEntity(player, uuid, type);
    entityMap.put(entityId, packetEntity);
  }

  public PacketEntity getEntity(int entityId) {
    return entityId == player.getEntityId() ? self : entityMap.get(entityId);
  }

  public void removeEntity(int entityId) {
    entityMap.remove(entityId);
  }

  public void clear() {
    entityMap.clear();
  }
}
