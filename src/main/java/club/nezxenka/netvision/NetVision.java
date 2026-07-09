package club.nezxenka.netvision;

import club.nezxenka.netvision.alert.internal.AlertManager;
import club.nezxenka.netvision.command.framework.CommandFramework;
import club.nezxenka.netvision.config.core.ConfigManager;
import club.nezxenka.netvision.config.locale.LocaleManager;
import club.nezxenka.netvision.database.connection.DatabaseManager;
import club.nezxenka.netvision.debug.internal.DebugManager;
import club.nezxenka.netvision.event.listener.DamageEventListener;
import club.nezxenka.netvision.hologram.internal.HologramManager;
import club.nezxenka.netvision.integration.worldguard.WorldGuardManager;
import club.nezxenka.netvision.listener.menu.MenuClickListener;
import club.nezxenka.netvision.menu.chickencoop.ChickenCoopMenu;
import club.nezxenka.netvision.menu.history.HistoryMenu;
import club.nezxenka.netvision.packet.listener.PacketListener;
import club.nezxenka.netvision.player.manager.PlayerDataManager;
import club.nezxenka.netvision.redis.alert.CrossServerAlertService;
import club.nezxenka.netvision.redis.connection.RedisManager;
import club.nezxenka.netvision.redis.suspicious.CrossServerSuspiciousService;
import club.nezxenka.netvision.server.provider.AIServerProvider;
import club.nezxenka.netvision.util.message.MessageUtil;
import com.github.retrooper.packetevents.PacketEvents;
import io.github.retrooper.packetevents.factory.spigot.SpigotPacketEventsBuilder;
import lombok.Getter;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.bukkit.plugin.java.JavaPlugin;

public final class NetVision extends JavaPlugin {

  private LocaleManager localeManager;
  private AIServerProvider aiServerProvider;
  private WorldGuardManager worldGuardManager;
  private AlertManager alertManager;

  @Getter PlayerDataManager playerDataManager;

  @Getter DatabaseManager databaseManager;

  @Getter private ConfigManager configManager;

  @Getter private ChickenCoopMenu chickenCoopMenu;

  @Getter private HistoryMenu historyMenu;

  @Getter private HologramManager hologramManager;

  @Getter private DebugManager debugManager;

  @Getter private BukkitAudiences adventure;

  private RedisManager redisManager;
  private CrossServerAlertService crossServerAlertService;
  private CrossServerSuspiciousService crossServerSuspiciousService;

  @Override
  public void onLoad() {
    PacketEvents.setAPI(SpigotPacketEventsBuilder.build(this));
    PacketEvents.getAPI().getSettings().checkForUpdates(false).bStats(true);
    PacketEvents.getAPI().load();
  }

  @Override
  public void onEnable() {
    this.adventure = BukkitAudiences.create(this);
    this.configManager = new ConfigManager(this);
    this.localeManager = new LocaleManager(this, configManager);
    this.debugManager = new DebugManager(this, configManager);
    MessageUtil.init(this.localeManager, this.adventure);
    this.databaseManager = new DatabaseManager(this, configManager);
    this.worldGuardManager = new WorldGuardManager(this, configManager);
    this.alertManager = new AlertManager(this, configManager, localeManager, adventure);
    this.aiServerProvider = new AIServerProvider(this, configManager);
    this.chickenCoopMenu = new ChickenCoopMenu(this);
    this.historyMenu = new HistoryMenu(this);
    this.hologramManager = new HologramManager(this);
    this.playerDataManager =
        new PlayerDataManager(
            this,
            alertManager,
            configManager,
            databaseManager,
            this.aiServerProvider,
            worldGuardManager);
    PacketEvents.getAPI()
        .getEventManager()
        .registerListener(new PacketListener(this.playerDataManager));
    PacketEvents.getAPI().init();
    this.redisManager = new RedisManager(configManager, getLogger());
    this.crossServerAlertService =
        new CrossServerAlertService(
            configManager, this.redisManager, alertManager, this, getLogger());
    this.crossServerAlertService.start();
    this.crossServerSuspiciousService =
        new CrossServerSuspiciousService(
            configManager, this.redisManager, playerDataManager, this, getLogger());
    this.crossServerSuspiciousService.start();
    new CommandFramework(
        this,
        alertManager,
        databaseManager,
        configManager,
        localeManager,
        playerDataManager,
        historyMenu);
    getServer().getPluginManager().registerEvents(new DamageEventListener(playerDataManager), this);
    getServer()
        .getPluginManager()
        .registerEvents(new MenuClickListener(this, chickenCoopMenu, historyMenu), this);
  }

  public void reloadPlugin() {
    configManager.reloadConfig();
    localeManager.reload();
    debugManager.reload();
    alertManager.reload();
    aiServerProvider.reload();
    if (playerDataManager != null) {
      playerDataManager.reloadAllPlayers();
    }
  }

  @Override
  public void onDisable() {
    if (hologramManager != null) {
      hologramManager.shutdown();
    }
    if (this.adventure != null) {
      this.adventure.close();
      this.adventure = null;
    }
    if (databaseManager != null) {
      databaseManager.shutdown();
    }
    if (PacketEvents.getAPI().isInitialized()) {
      PacketEvents.getAPI().terminate();
    }
    if (crossServerSuspiciousService != null) {
      crossServerSuspiciousService.shutdown();
    }
    if (crossServerAlertService != null) {
      crossServerAlertService.shutdown();
    }
    if (redisManager != null) {
      redisManager.shutdown();
    }
  }
}
