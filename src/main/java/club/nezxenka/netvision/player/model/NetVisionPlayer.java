package club.nezxenka.netvision.player.model;

import club.nezxenka.netvision.NetVision;
import club.nezxenka.netvision.alert.internal.AlertManager;
import club.nezxenka.netvision.check.registry.CheckManager;
import club.nezxenka.netvision.config.core.ConfigManager;
import club.nezxenka.netvision.database.connection.DatabaseManager;
import club.nezxenka.netvision.entity.compensation.CompensatedEntities;
import club.nezxenka.netvision.integration.worldguard.WorldGuardManager;
import club.nezxenka.netvision.player.state.PlayerRotationData;
import club.nezxenka.netvision.player.state.PlayerTeleportData;
import club.nezxenka.netvision.punishment.internal.PunishmentManager;
import club.nezxenka.netvision.server.provider.AIServerProvider;
import club.nezxenka.netvision.util.collection.Pair;
import club.nezxenka.netvision.util.latency.ILatencyUtils;
import club.nezxenka.netvision.util.latency.LatencyUtils;
import club.nezxenka.netvision.util.rotation.HeadRotation;
import club.nezxenka.netvision.util.rotation.PacketStateData;
import club.nezxenka.netvision.util.rotation.RotationUpdate;
import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.protocol.player.ClientVersion;
import com.github.retrooper.packetevents.protocol.player.GameMode;
import com.github.retrooper.packetevents.protocol.player.User;
import it.unimi.dsi.fastutil.ints.IntArraySet;
import java.util.Queue;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

@Getter
public class NetVisionPlayer {
  private final UUID uuid;
  private final Player player;
  private final User user;
  private final CheckManager checkManager;
  private final PunishmentManager punishmentManager;
  public final CompensatedEntities compensatedEntities;
  public final ILatencyUtils latencyUtils;
  public final PacketStateData packetStateData = new PacketStateData();
  public final RotationUpdate rotationUpdate =
      new RotationUpdate(new HeadRotation(), new HeadRotation(), 0, 0);
  public final long joinTime;
  @Setter private int entityId;
  @Setter private GameMode gameMode = GameMode.SURVIVAL;
  @Setter private String brand = "vanilla";
  @Setter private boolean bedrock = false;

  public boolean isBedrockExempt() {
    return plugin.getConfigManager().isBedrockExemptEnabled() && bedrock;
  }

  public double x, y, z;
  public float yaw, pitch;
  public float lastYaw, lastPitch;

  private final Queue<PlayerTeleportData> pendingTeleports = new ConcurrentLinkedQueue<>();
  private final Queue<PlayerRotationData> pendingRotations = new ConcurrentLinkedQueue<>();

  @Setter private double dmgMultiplier = 1.0;
  public int ticksSinceAttack;

  public final Queue<Pair<Short, Long>> transactionsSent = new ConcurrentLinkedQueue<>();
  public final IntArraySet entitiesDespawnedThisTransaction = new IntArraySet();
  public final Set<Short> didWeSendThatTrans = ConcurrentHashMap.newKeySet();
  public final AtomicInteger lastTransactionSent = new AtomicInteger(0);
  public final AtomicInteger lastTransactionReceived = new AtomicInteger(0);
  private final AtomicInteger transactionIDCounter = new AtomicInteger(0);
  private final NetVision plugin;

  public NetVisionPlayer(
      Player player,
      NetVision plugin,
      ConfigManager configManager,
      DatabaseManager databaseManager,
      AlertManager alertManager,
      AIServerProvider aiServerProvider,
      WorldGuardManager worldGuardManager) {
    this.plugin = plugin;
    this.player = player;
    this.uuid = player.getUniqueId();
    this.user = PacketEvents.getAPI().getPlayerManager().getUser(player);
    this.joinTime = System.currentTimeMillis();
    this.latencyUtils = new LatencyUtils(this, plugin);
    this.compensatedEntities = new CompensatedEntities(this);
    this.checkManager =
        new CheckManager(
            this, plugin, configManager, aiServerProvider, worldGuardManager, alertManager);
    this.punishmentManager =
        new PunishmentManager(
            this, plugin, configManager, databaseManager.getDatabase(), alertManager);
    int sequence = configManager.getAiSequence();
    this.ticksSinceAttack = sequence + 1;
  }

  public boolean isPointThree() {
    return getUser().getClientVersion().isOlderThan(ClientVersion.V_1_18_2);
  }

  public double getMovementThreshold() {
    return isPointThree() ? 0.03 : 0.0002;
  }

  public boolean isCancelDuplicatePacket() {
    return true;
  }

  public void sendTransaction() {
    if (user.getConnectionState()
        != com.github.retrooper.packetevents.protocol.ConnectionState.PLAY) return;
    short transactionID = (short) (-1 * (transactionIDCounter.getAndIncrement() & 0x7FFF));
    didWeSendThatTrans.add(transactionID);
    com.github.retrooper.packetevents.wrapper.PacketWrapper<?> packet;
    if (PacketEvents.getAPI()
        .getServerManager()
        .getVersion()
        .isNewerThanOrEquals(
            com.github.retrooper.packetevents.manager.server.ServerVersion.V_1_17)) {
      packet =
          new com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerPing(
              transactionID);
    } else {
      packet =
          new com.github.retrooper.packetevents.wrapper.play.server
              .WrapperPlayServerWindowConfirmation((byte) 0, transactionID, false);
    }
    user.sendPacket(packet);
  }

  public void disconnect(Component reason) {
    String textReason =
        net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer.legacySection()
            .serialize(reason);
    user.sendPacket(
        new com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerDisconnect(
            reason));
    user.closeConnection();
    if (Bukkit.isPrimaryThread()) player.kickPlayer(textReason);
    else Bukkit.getScheduler().runTask(plugin, () -> player.kickPlayer(textReason));
  }

  public void reload() {
    if (this.punishmentManager != null) this.punishmentManager.reload();
    if (this.checkManager != null) this.checkManager.reloadChecks();
  }
}
