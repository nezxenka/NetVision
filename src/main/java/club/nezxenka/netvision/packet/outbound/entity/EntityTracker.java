package club.nezxenka.netvision.packet.outbound.entity;

import club.nezxenka.netvision.player.model.NetVisionPlayer;

public class EntityTracker {
  public void onSpawn(
      NetVisionPlayer player,
      int entityId,
      java.util.UUID uuid,
      com.github.retrooper.packetevents.protocol.entity.type.EntityType type) {
    if (player.entitiesDespawnedThisTransaction.contains(entityId)) player.sendTransaction();
    player
        .getLatencyUtils()
        .addRealTimeTask(
            player.getLastTransactionSent().get(),
            () -> player.getCompensatedEntities().addEntity(entityId, uuid, type));
  }

  public void onDestroy(NetVisionPlayer player, int[] entityIds) {
    for (int id : entityIds) player.entitiesDespawnedThisTransaction.add(id);
    player
        .getLatencyUtils()
        .addRealTimeTask(
            player.getLastTransactionSent().get() + 1,
            () -> {
              for (int id : entityIds) player.getCompensatedEntities().removeEntity(id);
            });
  }
}
