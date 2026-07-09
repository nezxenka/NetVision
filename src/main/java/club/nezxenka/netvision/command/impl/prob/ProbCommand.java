package club.nezxenka.netvision.command.impl.prob;

import club.nezxenka.netvision.NetVision;
import club.nezxenka.netvision.check.packet.ai.internal.AICheck;
import club.nezxenka.netvision.command.api.NetVisionCommand;
import club.nezxenka.netvision.command.framework.CommandRegistrationService;
import club.nezxenka.netvision.command.requirement.PlayerSenderRequirement;
import club.nezxenka.netvision.config.locale.LocaleManager;
import club.nezxenka.netvision.player.manager.PlayerDataManager;
import club.nezxenka.netvision.player.model.NetVisionPlayer;
import club.nezxenka.netvision.sender.api.Sender;
import club.nezxenka.netvision.util.message.Message;
import club.nezxenka.netvision.util.message.MessageUtil;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitTask;
import org.incendo.cloud.CommandManager;
import org.incendo.cloud.bukkit.parser.PlayerParser;
import org.incendo.cloud.context.CommandContext;

public class ProbCommand implements NetVisionCommand, Listener {
  private final Map<UUID, ProbSession> activeSessions = new ConcurrentHashMap<>();
  private final PlayerDataManager playerDataManager;
  private final LocaleManager localeManager;
  private final NetVision plugin;

  public ProbCommand(
      PlayerDataManager playerDataManager, LocaleManager localeManager, NetVision plugin) {
    this.playerDataManager = playerDataManager;
    this.localeManager = localeManager;
    this.plugin = plugin;
    plugin.getServer().getPluginManager().registerEvents(this, plugin);
  }

  @Override
  public void register(CommandManager<Sender> manager, String rootName) {
    manager.command(
        manager
            .commandBuilder(rootName)
            .literal("prob")
            .permission("netvision.prob")
            .required("target", PlayerParser.playerParser())
            .apply(
                CommandRegistrationService.REQUIREMENT_FACTORY.create(
                    PlayerSenderRequirement.PLAYER_SENDER_REQUIREMENT))
            .handler(this::execute));
  }

  @EventHandler
  public void onPlayerQuit(PlayerQuitEvent event) {
    final Player player = event.getPlayer();
    final UUID uuid = player.getUniqueId();
    if (activeSessions.containsKey(uuid)) stop(player);
    UUID viewerUuid = null;
    for (Map.Entry<UUID, ProbSession> entry : activeSessions.entrySet()) {
      if (entry.getValue().targetUuid().equals(uuid)) {
        viewerUuid = entry.getKey();
        break;
      }
    }
    if (viewerUuid != null) {
      Player viewer = Bukkit.getPlayer(viewerUuid);
      if (viewer != null) {
        stop(viewer);
        MessageUtil.sendMessage(viewer, Message.PROB_DISABLED, "player", player.getName());
      } else activeSessions.remove(viewerUuid);
    }
  }

  private void execute(CommandContext<Sender> context) {
    final Player player = context.sender().getPlayer();
    final Player target = context.get("target");
    final ProbSession session = activeSessions.get(player.getUniqueId());
    if (session != null && session.targetUuid().equals(target.getUniqueId())) {
      stop(player);
      MessageUtil.sendMessage(player, Message.PROB_DISABLED, "player", target.getName());
      return;
    }
    if (session != null) stop(player);
    start(player, target);
    MessageUtil.sendMessage(player, Message.PROB_ENABLED, "player", target.getName());
  }

  private void start(Player viewer, Player target) {
    final UUID viewerId = viewer.getUniqueId();
    final UUID targetId = target.getUniqueId();
    final ActionBarComponents components = new ActionBarComponents(localeManager);
    final BukkitTask task =
        plugin
            .getServer()
            .getScheduler()
            .runTaskTimer(
                plugin,
                () -> {
                  final Player onlineViewer = Bukkit.getPlayer(viewerId);
                  final Player onlineTarget = Bukkit.getPlayer(targetId);
                  if (onlineViewer == null
                      || !onlineViewer.isOnline()
                      || onlineTarget == null
                      || !onlineTarget.isOnline()) {
                    if (onlineViewer != null) stop(onlineViewer);
                    return;
                  }
                  final NetVisionPlayer nvTarget = playerDataManager.getPlayer(onlineTarget);
                  if (nvTarget == null) {
                    sendActionBar(
                        onlineViewer,
                        MessageUtil.getMessage(
                            Message.PROB_NO_DATA, "player", onlineTarget.getName()));
                    return;
                  }
                  final AICheck aiCheck = nvTarget.getCheckManager().getCheck(AICheck.class);
                  if (aiCheck == null) {
                    sendActionBar(
                        onlineViewer,
                        MessageUtil.getMessage(
                            Message.PROB_NO_AICHECK, "player", onlineTarget.getName()));
                    return;
                  }
                  sendActionBar(onlineViewer, buildActionBar(aiCheck, onlineTarget, components));
                },
                0L,
                2L);
    final ProbSession newSession = new ProbSession(targetId, task, components);
    activeSessions.put(viewerId, newSession);
  }

  private void stop(Player viewer) {
    final ProbSession session = activeSessions.remove(viewer.getUniqueId());
    if (session != null) {
      session.task().cancel();
      sendActionBar(viewer, Component.empty());
    }
  }

  private Component buildActionBar(AICheck aiCheck, Player target, ActionBarComponents components) {
    final double probability = aiCheck.getLastProbability();
    final double violationLevel = aiCheck.getBuffer();
    final int ping = target.getPing();
    final TextColor probColor = getProbColor(probability);
    final TextColor vlColor = getVlColor(violationLevel);
    final TextColor pingColor = getPingColor(ping);
    TextComponent bufferComponent =
        Component.text(String.format(Locale.US, "%.2f", violationLevel), vlColor);
    if (violationLevel > 30) bufferComponent = bufferComponent.decorate(TextDecoration.BOLD);
    return Component.text()
        .append(components.labelProb().color(probColor))
        .append(components.openParen().color(probColor))
        .append(Component.text(target.getName(), probColor))
        .append(components.closeParen().color(probColor))
        .append(Component.text(String.format(Locale.US, "%.4f", probability), probColor))
        .append(components.separator())
        .append(components.labelBuffer().color(vlColor))
        .append(components.colon().color(vlColor))
        .append(bufferComponent)
        .append(components.separator())
        .append(components.labelPing().color(pingColor))
        .append(components.colon().color(pingColor))
        .append(Component.text(ping, pingColor))
        .append(components.suffixPing().color(pingColor))
        .build();
  }

  private void sendActionBar(Player player, Component message) {
    if (player == null || !player.isOnline()) return;
    plugin.getAdventure().player(player).sendActionBar(message);
  }

  private TextColor getProbColor(double probability) {
    if (probability > 0.9) return NamedTextColor.RED;
    if (probability > 0.5) return NamedTextColor.YELLOW;
    return NamedTextColor.GREEN;
  }

  private TextColor getVlColor(double violationLevel) {
    if (violationLevel > 30) return NamedTextColor.DARK_RED;
    if (violationLevel > 15) return NamedTextColor.RED;
    return NamedTextColor.GREEN;
  }

  private TextColor getPingColor(int ping) {
    if (ping > 150) return NamedTextColor.RED;
    if (ping > 80) return NamedTextColor.YELLOW;
    return NamedTextColor.GREEN;
  }

  private record ProbSession(UUID targetUuid, BukkitTask task, ActionBarComponents components) {}

  private record ActionBarComponents(
      Component labelProb,
      Component labelBuffer,
      Component labelPing,
      Component separator,
      Component suffixPing,
      Component openParen,
      Component closeParen,
      Component colon) {
    ActionBarComponents(LocaleManager lm) {
      this(
          Component.text(lm.getRawMessage(Message.PROB_FORMAT_LABEL_PROB)),
          Component.text(lm.getRawMessage(Message.PROB_FORMAT_LABEL_BUFFER)),
          Component.text(lm.getRawMessage(Message.PROB_FORMAT_LABEL_PING)),
          Component.text(lm.getRawMessage(Message.PROB_FORMAT_SEPARATOR), NamedTextColor.DARK_GRAY),
          Component.text(lm.getRawMessage(Message.PROB_FORMAT_SUFFIX_PING)),
          Component.text(" ("),
          Component.text("): "),
          Component.text(": "));
    }
  }
}
