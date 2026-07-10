package club.nezxenka.netvision.service.enforce.internal;

import club.nezxenka.netvision.NetVision;
import club.nezxenka.netvision.actor.model.NetVisionPlayer;
import club.nezxenka.netvision.core.config.ConfigManager;
import club.nezxenka.netvision.core.storage.api.RecordStorage;
import club.nezxenka.netvision.engine.api.AnalysisModule;
import club.nezxenka.netvision.service.enforce.model.EnforcementGroup;
import club.nezxenka.netvision.service.signal.internal.SignalManager;
import club.nezxenka.netvision.service.signal.model.SignalType;
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

public class EnforcementManager {
  private final NetVisionPlayer nvPlayer;
  private final NetVision plugin;
  private final ConfigManager configManager;
  private final Map<String, EnforcementGroup> punishmentGroups = new HashMap<>();
  private final SignalManager alertManager;
  private final RecordStorage database;

  public EnforcementManager(
      NetVisionPlayer nvPlayer,
      NetVision plugin,
      ConfigManager configManager,
      RecordStorage database,
      SignalManager alertManager) {
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
      List<String> moduleNamesFilters = groupSection.getStringList("checks");
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
        EnforcementGroup punishGroup =
            new EnforcementGroup(groupName, moduleNamesFilters, parsedActions);
        punishmentGroups.put(groupName, punishGroup);
      }
    }
  }

  public void handleFlag(AnalysisModule module, String debug) {
    for (EnforcementGroup group : punishmentGroups.values()) {
      if (group.isModuleAssociated(module)) {
        Bukkit.getScheduler()
            .runTaskAsynchronously(
                plugin,
                () -> {
                  int newVl =
                      database.incrementViolationLevel(nvPlayer.getUuid(), group.getGroupName());
                  Map.Entry<Integer, List<String>> entry = group.getActions().floorEntry(newVl);
                  if (entry != null) executeCommands(module, group, newVl, debug, entry.getValue());
                });
      }
    }
  }

  private void executeCommands(
      AnalysisModule module,
      EnforcementGroup group,
      int vl,
      String verbose,
      List<String> commands) {
    for (String command : commands) {
      String commandLower = command.toLowerCase(Locale.ROOT);
      if (commandLower.equals("[alert]")) sendAlert(module, vl, verbose);
      else if (commandLower.equals("[log]"))
        database.logAlert(nvPlayer, verbose, module.getModuleName(), vl);
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
                module.getModuleName(),
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
                .replace("<check_name>", module.getModuleName())
                .replace("<vl>", String.valueOf(vl))
                .replace("<verbose>", verbose);
        Bukkit.getScheduler()
            .runTask(plugin, () -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), formattedCmd));
      }
    }
  }

  private void sendAlert(AnalysisModule module, int vl, String verbose) {
    final Component message =
        MessageUtil.getMessage(
            Message.ALERTS_FORMAT,
            "player",
            nvPlayer.getPlayer().getName(),
            "check_name",
            module.getModuleName(),
            "vl",
            String.valueOf(vl),
            "verbose",
            verbose);
    Bukkit.getScheduler().runTask(plugin, () -> alertManager.send(message, SignalType.REGULAR));
  }
}
