package club.nezxenka.netvision.punishment.internal;

import club.nezxenka.netvision.NetVision;
import club.nezxenka.netvision.alert.internal.AlertManager;
import club.nezxenka.netvision.alert.model.AlertType;
import club.nezxenka.netvision.check.api.Check;
import club.nezxenka.netvision.config.core.ConfigManager;
import club.nezxenka.netvision.database.api.ViolationDatabase;
import club.nezxenka.netvision.player.model.NetVisionPlayer;
import club.nezxenka.netvision.punishment.model.PunishGroup;
import club.nezxenka.netvision.util.message.Message;
import club.nezxenka.netvision.util.message.MessageUtil;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;

public class PunishmentManager {
  private final NetVisionPlayer nvPlayer;
  private final NetVision plugin;
  private final ConfigManager configManager;
  private final Map<String, PunishGroup> punishmentGroups = new HashMap<>();
  private final AlertManager alertManager;
  private final ViolationDatabase database;

  public PunishmentManager(
      NetVisionPlayer nvPlayer,
      NetVision plugin,
      ConfigManager configManager,
      ViolationDatabase database,
      AlertManager alertManager) {
    this.nvPlayer = nvPlayer;
    this.plugin = plugin;
    this.configManager = configManager;
    this.alertManager = alertManager;
    this.database = database;
    reload();
  }

  public void reload() {
    punishmentGroups.clear();
    ConfigurationSection punishmentsSection =
        configManager.getPunishments().getConfigurationSection("Punishments");
    if (punishmentsSection == null) return;
    for (String groupName : punishmentsSection.getKeys(false)) {
      ConfigurationSection groupSection = punishmentsSection.getConfigurationSection(groupName);
      if (groupSection == null) continue;
      List<String> checkNamesFilters = groupSection.getStringList("checks");
      ConfigurationSection actionsSection = groupSection.getConfigurationSection("actions");
      if (actionsSection == null) continue;
      NavigableMap<Integer, List<String>> parsedActions = new TreeMap<>();
      for (String vlString : actionsSection.getKeys(false)) {
        try {
          int vl = Integer.parseInt(vlString);
          List<String> commands = actionsSection.getStringList(vlString);
          parsedActions.put(vl, commands);
        } catch (NumberFormatException e) {
          plugin
              .getLogger()
              .warning("Invalid VL " + vlString + " in punishment group " + groupName + ".");
        }
      }
      if (!parsedActions.isEmpty()) {
        PunishGroup punishGroup = new PunishGroup(groupName, checkNamesFilters, parsedActions);
        punishmentGroups.put(groupName, punishGroup);
      }
    }
  }

  public void handleFlag(Check check, String debug) {
    for (PunishGroup group : punishmentGroups.values()) {
      if (group.isCheckAssociated(check)) {
        Bukkit.getScheduler()
            .runTaskAsynchronously(
                plugin,
                () -> {
                  int newVl =
                      database.incrementViolationLevel(nvPlayer.getUuid(), group.getGroupName());
                  Map.Entry<Integer, List<String>> entry = group.getActions().floorEntry(newVl);
                  if (entry != null) executeCommands(check, group, newVl, debug, entry.getValue());
                });
      }
    }
  }

  private void executeCommands(
      Check check, PunishGroup group, int vl, String verbose, List<String> commands) {
    for (String command : commands) {
      String commandLower = command.toLowerCase(Locale.ROOT);
      if (commandLower.equals("[alert]")) sendAlert(check, vl, verbose);
      else if (commandLower.equals("[log]"))
        database.logAlert(nvPlayer, verbose, check.getCheckName(), vl);
      else if (commandLower.equals("[reset]"))
        database.resetViolationLevel(nvPlayer.getUuid(), group.getGroupName());
      else if (commandLower.startsWith("[broadcast] ")) {
        final String message = command.substring("[broadcast] ".length());
        final Component component =
            MessageUtil.format(
                message,
                "player",
                nvPlayer.getPlayer().getName(),
                "check_name",
                check.getCheckName(),
                "vl",
                String.valueOf(vl),
                "verbose",
                verbose);
        Bukkit.getScheduler()
            .runTask(plugin, () -> plugin.getAdventure().players().sendMessage(component));
      } else {
        String formattedCmd =
            command
                .replace("<player>", nvPlayer.getPlayer().getName())
                .replace("<check_name>", check.getCheckName())
                .replace("<vl>", String.valueOf(vl))
                .replace("<verbose>", verbose);
        Bukkit.getScheduler()
            .runTask(plugin, () -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), formattedCmd));
      }
    }
  }

  private void sendAlert(Check check, int vl, String verbose) {
    final Component message =
        MessageUtil.getMessage(
            Message.ALERTS_FORMAT,
            "player",
            nvPlayer.getPlayer().getName(),
            "check_name",
            check.getCheckName(),
            "vl",
            String.valueOf(vl),
            "verbose",
            verbose);
    Bukkit.getScheduler().runTask(plugin, () -> alertManager.send(message, AlertType.REGULAR));
  }
}
