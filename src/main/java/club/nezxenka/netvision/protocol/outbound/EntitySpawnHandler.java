package club.nezxenka.netvision.protocol.outbound;

import club.nezxenka.netvision.actor.model.NetVisionPlayer;
import com.github.retrooper.packetevents.protocol.entity.type.EntityTypes;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerDestroyEntities;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSpawnEntity;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSpawnLivingEntity;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSpawnPainting;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSpawnPlayer;

public class EntitySpawnHandler {
  public void handleSpawnEntity(WrapperPlayServerSpawnEntity spawn, NetVisionPlayer nvPlayer) {
    if (nvPlayer.entitiesDespawnedThisTransaction.contains(spawn.getEntityId()))
      nvPlayer.sendTransaction();
    nvPlayer
        .getLatencyUtils()
        .addRealTimeTask(
            nvPlayer.getLastTransactionSent().get(),
            () ->
                nvPlayer
                    .getCompensatedEntities()
                    .addEntity(
                        spawn.getEntityId(), spawn.getUUID().orElse(null), spawn.getEntityType()));
  }

  public void handleSpawnLivingEntity(
      WrapperPlayServerSpawnLivingEntity spawn, NetVisionPlayer nvPlayer) {
    if (nvPlayer.entitiesDespawnedThisTransaction.contains(spawn.getEntityId()))
      nvPlayer.sendTransaction();
    nvPlayer
        .getLatencyUtils()
        .addRealTimeTask(
            nvPlayer.getLastTransactionSent().get(),
            () ->
                nvPlayer
                    .getCompensatedEntities()
                    .addEntity(spawn.getEntityId(), spawn.getEntityUUID(), spawn.getEntityType()));
  }

  public void handleSpawnPainting(WrapperPlayServerSpawnPainting spawn, NetVisionPlayer nvPlayer) {
    if (nvPlayer.entitiesDespawnedThisTransaction.contains(spawn.getEntityId()))
      nvPlayer.sendTransaction();
    nvPlayer
        .getLatencyUtils()
        .addRealTimeTask(
            nvPlayer.getLastTransactionSent().get(),
            () ->
                nvPlayer
                    .getCompensatedEntities()
                    .addEntity(spawn.getEntityId(), spawn.getUUID(), EntityTypes.PAINTING));
  }

  public void handleSpawnPlayer(WrapperPlayServerSpawnPlayer spawn, NetVisionPlayer nvPlayer) {
    if (nvPlayer.entitiesDespawnedThisTransaction.contains(spawn.getEntityId()))
      nvPlayer.sendTransaction();
    nvPlayer
        .getLatencyUtils()
        .addRealTimeTask(
            nvPlayer.getLastTransactionSent().get(),
            () ->
                nvPlayer
                    .getCompensatedEntities()
                    .addEntity(spawn.getEntityId(), spawn.getUUID(), EntityTypes.PLAYER));
  }

  public void handleDestroyEntities(
      WrapperPlayServerDestroyEntities destroy, NetVisionPlayer nvPlayer) {
    for (int id : destroy.getEntityIds()) nvPlayer.entitiesDespawnedThisTransaction.add(id);
    nvPlayer
        .getLatencyUtils()
        .addRealTimeTask(
            nvPlayer.getLastTransactionSent().get() + 1,
            () -> {
              for (int id : destroy.getEntityIds())
                nvPlayer.getCompensatedEntities().removeEntity(id);
            });
  }
}
