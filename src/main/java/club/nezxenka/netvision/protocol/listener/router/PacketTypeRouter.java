package club.nezxenka.netvision.protocol.listener.router;

import com.github.retrooper.packetevents.protocol.packettype.PacketType;

public class PacketTypeRouter {
  public boolean isWindowConfirmation(PacketType.Play.Server type) {
    return type == PacketType.Play.Server.WINDOW_CONFIRMATION;
  }

  public boolean isPing(PacketType.Play.Server type) {
    return type == PacketType.Play.Server.PING;
  }

  public boolean isSpawnEntity(PacketType.Play.Server type) {
    return type == PacketType.Play.Server.SPAWN_ENTITY;
  }

  public boolean isSpawnLivingEntity(PacketType.Play.Server type) {
    return type == PacketType.Play.Server.SPAWN_LIVING_ENTITY;
  }

  public boolean isSpawnPainting(PacketType.Play.Server type) {
    return type == PacketType.Play.Server.SPAWN_PAINTING;
  }

  public boolean isSpawnPlayer(PacketType.Play.Server type) {
    return type == PacketType.Play.Server.SPAWN_PLAYER;
  }

  public boolean isDestroyEntities(PacketType.Play.Server type) {
    return type == PacketType.Play.Server.DESTROY_ENTITIES;
  }

  public boolean isJoinGame(PacketType.Play.Server type) {
    return type == PacketType.Play.Server.JOIN_GAME;
  }

  public boolean isRespawn(PacketType.Play.Server type) {
    return type == PacketType.Play.Server.RESPAWN;
  }

  public boolean isPositionAndLook(PacketType.Play.Server type) {
    return type == PacketType.Play.Server.PLAYER_POSITION_AND_LOOK;
  }

  public boolean isPlayerRotation(PacketType.Play.Server type) {
    return type == PacketType.Play.Server.PLAYER_ROTATION;
  }
}
