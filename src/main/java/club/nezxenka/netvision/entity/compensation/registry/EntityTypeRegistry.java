package club.nezxenka.netvision.entity.compensation.registry;

import club.nezxenka.netvision.actor.model.NetVisionPlayer;
import club.nezxenka.netvision.entity.api.PacketEntity;
import com.github.retrooper.packetevents.protocol.entity.type.EntityType;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.BiFunction;

public class EntityTypeRegistry {
  private final Map<EntityType, BiFunction<NetVisionPlayer, UUID, PacketEntity>> factories =
      new HashMap<>();

  public void register(EntityType type, BiFunction<NetVisionPlayer, UUID, PacketEntity> factory) {
    factories.put(type, factory);
  }

  public PacketEntity create(EntityType type, NetVisionPlayer player, UUID uuid) {
    BiFunction<NetVisionPlayer, UUID, PacketEntity> factory = factories.get(type);
    return factory != null ? factory.apply(player, uuid) : new PacketEntity(player, uuid, type);
  }
}
