package club.nezxenka.netvision.service.signal.internal;

import club.nezxenka.netvision.NetVision;
import club.nezxenka.netvision.core.config.ConfigManager;
import club.nezxenka.netvision.core.locale.LocaleManager;
import club.nezxenka.netvision.service.signal.model.SignalType;
import club.nezxenka.netvision.util.message.Message;
import club.nezxenka.netvision.util.message.MessageUtil;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArraySet;
import lombok.Getter;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SignalManager {

  private final ConfigManager configManager;
  private final LocaleManager localeManager;
  private final BukkitAudiences adventure;
  private final Map<SignalType, Set<UUID>> playersWithAlerts = new EnumMap<>(SignalType.class);
  private final Set<SignalType> consoleAlertsEnabled = EnumSet.allOf(SignalType.class);
  private boolean logToConsole;

  @Getter private String alertFormat;

  @Getter private String brandAlertFormat;

  private volatile club.nezxenka.netvision.service.bridge.alert.CrossServerPublisher
      crossServerPublisher;

  public void setCrossServerPublisher(
      club.nezxenka.netvision.service.bridge.alert.CrossServerPublisher publisher) {
    this.crossServerPublisher = publisher;
  }

  public SignalManager(
      NetVision plugin,
      ConfigManager configManager,
      LocaleManager localeManager,
      BukkitAudiences adventure) {
    this.configManager = configManager;
    this.localeManager = localeManager;
    this.adventure = adventure;
    for (SignalType type : SignalType.values())
      playersWithAlerts.put(type, new CopyOnWriteArraySet<>());
    reload();
  }

  public void reload() {
    this.logToConsole = configManager.getConfig().getBoolean("alerts.print-to-console", true);
    this.alertFormat = localeManager.getRawMessage(Message.ALERTS_FORMAT);
    this.brandAlertFormat = localeManager.getRawMessage(Message.BRAND_NOTIFICATION);
  }

  public void toggle(Player player, SignalType type, boolean silent) {
    Set<UUID> playersSet = playersWithAlerts.get(type);
    UUID uuid = player.getUniqueId();
    if (playersSet.contains(uuid)) {
      playersSet.remove(uuid);
      if (!silent) adventure(player).sendMessage(MessageUtil.getMessage(type.getDisabledMessage()));
    } else {
      playersSet.add(uuid);
      if (!silent) adventure(player).sendMessage(MessageUtil.getMessage(type.getEnabledMessage()));
    }
  }

  public void send(Component component, SignalType type) {
    deliver(component, type);
    club.nezxenka.netvision.service.bridge.alert.CrossServerPublisher publisher =
        this.crossServerPublisher;
    if (publisher != null) publisher.publish(type, component);
  }

  public void deliver(Component component, SignalType type) {
    Set<UUID> playersSet = playersWithAlerts.get(type);
    String permission = type.getPermission();
    for (UUID uuid : playersSet) {
      Player p = Bukkit.getPlayer(uuid);
      if (p != null && p.hasPermission(permission)) adventure(p).sendMessage(component);
    }
    if (logToConsole && consoleAlertsEnabled.contains(type))
      adventure(Bukkit.getConsoleSender()).sendMessage(component);
  }

  public boolean hasAlertsEnabled(Player player, SignalType type) {
    return playersWithAlerts.get(type).contains(player.getUniqueId());
  }

  public boolean isConsoleAlertsEnabled(SignalType type) {
    return consoleAlertsEnabled.contains(type);
  }

  public void toggleConsoleAlerts(SignalType type) {
    if (consoleAlertsEnabled.contains(type)) consoleAlertsEnabled.remove(type);
    else consoleAlertsEnabled.add(type);
  }

  public void handlePlayerQuit(Player player) {
    UUID uuid = player.getUniqueId();
    for (Set<UUID> players : playersWithAlerts.values()) players.remove(uuid);
  }

  private Audience adventure(Player player) {
    return adventure.player(player);
  }

  private Audience adventure(CommandSender sender) {
    return adventure.sender(sender);
  }
}
