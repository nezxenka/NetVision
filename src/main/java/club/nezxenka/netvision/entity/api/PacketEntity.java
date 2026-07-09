package club.nezxenka.netvision.entity.api;

import club.nezxenka.netvision.player.model.NetVisionPlayer;
import com.github.retrooper.packetevents.protocol.entity.type.EntityType;
import com.github.retrooper.packetevents.protocol.entity.type.EntityTypes;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Getter
public class PacketEntity {
  private final NetVisionPlayer player;
  private final UUID uuid;
  private final EntityType type;
  public final boolean isLivingEntity;
  public final boolean isPlayer;
  public final boolean isBoat;
  @Setter private PacketEntity riding = null;
  private final List<PacketEntity> passengers = new ArrayList<>(0);

  public PacketEntity(NetVisionPlayer player, UUID uuid, EntityType type) {
    this.player = player;
    this.uuid = uuid;
    this.type = type;
    this.isLivingEntity = EntityTypes.isTypeInstanceOf(type, EntityTypes.LIVINGENTITY);
    this.isPlayer = type == EntityTypes.PLAYER;
    this.isBoat = EntityTypes.isTypeInstanceOf(type, EntityTypes.BOAT);
  }

  public boolean inVehicle() {
    return this.riding != null;
  }

  public void mount(PacketEntity vehicle) {
    if (riding != null) eject();
    vehicle.passengers.add(this);
    this.riding = vehicle;
  }

  public void eject() {
    if (riding != null) riding.passengers.remove(this);
    this.riding = null;
  }
}
