package club.nezxenka.netvision.packet.listener;

import club.nezxenka.netvision.packet.inbound.FlyingPacketHandler;
import club.nezxenka.netvision.packet.inbound.TransactionPacketHandler;
import club.nezxenka.netvision.packet.outbound.EntitySpawnHandler;
import club.nezxenka.netvision.packet.outbound.JoinGameHandler;
import club.nezxenka.netvision.packet.outbound.RespawnHandler;
import club.nezxenka.netvision.packet.outbound.TeleportHandler;
import club.nezxenka.netvision.packet.queue.RotationQueueManager;
import club.nezxenka.netvision.packet.queue.TeleportQueueManager;
import club.nezxenka.netvision.packet.validation.DuplicatePacketFilter;
import club.nezxenka.netvision.player.manager.PlayerDataManager;
import club.nezxenka.netvision.player.model.NetVisionPlayer;
import club.nezxenka.netvision.util.collection.Pair;
import com.github.retrooper.packetevents.event.PacketListenerAbstract;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerFlying;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerDestroyEntities;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerJoinGame;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerPing;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerPlayerPositionAndLook;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerPlayerRotation;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSpawnEntity;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSpawnLivingEntity;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSpawnPainting;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSpawnPlayer;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerWindowConfirmation;
import org.bukkit.entity.Player;

public class PacketListener extends PacketListenerAbstract {
  private final PlayerDataManager playerDataManager;
  private final TeleportQueueManager teleportQueueManager = new TeleportQueueManager();
  private final RotationQueueManager rotationQueueManager = new RotationQueueManager();
  private final TransactionPacketHandler transactionHandler = new TransactionPacketHandler();
  private final EntitySpawnHandler entitySpawnHandler = new EntitySpawnHandler();
  private final JoinGameHandler joinGameHandler = new JoinGameHandler();
  private final RespawnHandler respawnHandler = new RespawnHandler();
  private final TeleportHandler teleportHandler = new TeleportHandler();
  private final DuplicatePacketFilter duplicateFilter = new DuplicatePacketFilter();

  public PacketListener(PlayerDataManager playerDataManager) {
    this.playerDataManager = playerDataManager;
  }

  @Override
  public void onPacketReceive(PacketReceiveEvent event) {
    if (!(event.getPlayer() instanceof Player)) return;
    NetVisionPlayer nvPlayer = playerDataManager.getPlayer((Player) event.getPlayer());
    if (nvPlayer == null) return;
    if (handleTransaction(event, nvPlayer)) return;
    if (WrapperPlayClientPlayerFlying.isFlying(event.getPacketType()))
      handleFlying(event, nvPlayer);
    if (event.isCancelled()) {
      resetFlags(nvPlayer);
      return;
    }
    if (nvPlayer.packetStateData.lastPacketWasTeleport
        || nvPlayer.packetStateData.lastPacketWasServerRotation)
      updatePlayerState(nvPlayer, new WrapperPlayClientPlayerFlying(event));
    nvPlayer.getCheckManager().onPacketReceive(event);
    resetFlags(nvPlayer);
  }

  private boolean handleTransaction(PacketReceiveEvent event, NetVisionPlayer nvPlayer) {
    return transactionHandler.handle(event, nvPlayer);
  }

  private void handleFlying(PacketReceiveEvent event, NetVisionPlayer nvPlayer) {
    WrapperPlayClientPlayerFlying flying = new WrapperPlayClientPlayerFlying(event);
    boolean teleported = teleportQueueManager.checkQueue(nvPlayer, flying);
    boolean serverRotated = !teleported && rotationQueueManager.checkQueue(nvPlayer, flying);
    nvPlayer.packetStateData.lastPacketWasTeleport = teleported;
    nvPlayer.packetStateData.lastPacketWasServerRotation = serverRotated;
    duplicateFilter.filterDuplicate(nvPlayer, flying, event);
    if (!event.isCancelled()) FlyingPacketHandler.processRotation(nvPlayer, flying);
  }

  private void updatePlayerState(NetVisionPlayer nvPlayer, WrapperPlayClientPlayerFlying flying) {
    if (flying.hasPositionChanged()) {
      nvPlayer.x = flying.getLocation().getX();
      nvPlayer.y = flying.getLocation().getY();
      nvPlayer.z = flying.getLocation().getZ();
    }
    if (flying.hasRotationChanged()) {
      nvPlayer.yaw = flying.getLocation().getYaw();
      nvPlayer.pitch = flying.getLocation().getPitch();
    }
  }

  private void resetFlags(NetVisionPlayer nvPlayer) {
    nvPlayer.packetStateData.lastPacketWasOnePointSeventeenDuplicate = false;
    nvPlayer.packetStateData.lastPacketWasTeleport = false;
    nvPlayer.packetStateData.lastPacketWasServerRotation = false;
  }

  @Override
  public void onPacketSend(PacketSendEvent event) {
    if (!(event.getPlayer() instanceof Player)) return;
    NetVisionPlayer nvPlayer = playerDataManager.getPlayer((Player) event.getPlayer());
    if (nvPlayer == null) return;
    if (!(event.getPacketType() instanceof PacketType.Play.Server)) return;
    final PacketType.Play.Server packetType = (PacketType.Play.Server) event.getPacketType();
    if (packetType == PacketType.Play.Server.WINDOW_CONFIRMATION)
      handleWindowConfirmation(new WrapperPlayServerWindowConfirmation(event), nvPlayer);
    else if (packetType == PacketType.Play.Server.PING)
      handlePing(new WrapperPlayServerPing(event), nvPlayer);
    else if (packetType == PacketType.Play.Server.SPAWN_ENTITY)
      entitySpawnHandler.handleSpawnEntity(new WrapperPlayServerSpawnEntity(event), nvPlayer);
    else if (packetType == PacketType.Play.Server.SPAWN_LIVING_ENTITY)
      entitySpawnHandler.handleSpawnLivingEntity(
          new WrapperPlayServerSpawnLivingEntity(event), nvPlayer);
    else if (packetType == PacketType.Play.Server.SPAWN_PAINTING)
      entitySpawnHandler.handleSpawnPainting(new WrapperPlayServerSpawnPainting(event), nvPlayer);
    else if (packetType == PacketType.Play.Server.SPAWN_PLAYER)
      entitySpawnHandler.handleSpawnPlayer(new WrapperPlayServerSpawnPlayer(event), nvPlayer);
    else if (packetType == PacketType.Play.Server.DESTROY_ENTITIES)
      entitySpawnHandler.handleDestroyEntities(
          new WrapperPlayServerDestroyEntities(event), nvPlayer);
    else if (packetType == PacketType.Play.Server.JOIN_GAME)
      joinGameHandler.handle(new WrapperPlayServerJoinGame(event), nvPlayer);
    else if (packetType == PacketType.Play.Server.RESPAWN) respawnHandler.handle(nvPlayer);
    else if (packetType == PacketType.Play.Server.PLAYER_POSITION_AND_LOOK)
      teleportHandler.handle(new WrapperPlayServerPlayerPositionAndLook(event), nvPlayer);
    else if (packetType == PacketType.Play.Server.PLAYER_ROTATION)
      teleportHandler.handleRotation(new WrapperPlayServerPlayerRotation(event), nvPlayer);
  }

  private void handleWindowConfirmation(
      WrapperPlayServerWindowConfirmation confirmation, NetVisionPlayer nvPlayer) {
    short id = confirmation.getActionId();
    if (id <= 0 && nvPlayer.didWeSendThatTrans.remove(id)) {
      nvPlayer.entitiesDespawnedThisTransaction.clear();
      nvPlayer.transactionsSent.add(new Pair<>(id, System.nanoTime()));
      nvPlayer.getLastTransactionSent().getAndIncrement();
    }
  }

  private void handlePing(WrapperPlayServerPing ping, NetVisionPlayer nvPlayer) {
    int id = ping.getId();
    if (id == (short) id && nvPlayer.didWeSendThatTrans.remove((short) id)) {
      nvPlayer.entitiesDespawnedThisTransaction.clear();
      nvPlayer.transactionsSent.add(new Pair<>((short) id, System.nanoTime()));
      nvPlayer.getLastTransactionSent().getAndIncrement();
    }
  }
}
