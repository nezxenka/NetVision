package club.nezxenka.netvision.command.framework;

import club.nezxenka.netvision.NetVision;
import club.nezxenka.netvision.alert.internal.AlertManager;
import club.nezxenka.netvision.command.api.SenderRequirement;
import club.nezxenka.netvision.command.failure.CommandFailureHandler;
import club.nezxenka.netvision.command.impl.alerts.AlertsCommand;
import club.nezxenka.netvision.command.impl.ban.NvpBanCommand;
import club.nezxenka.netvision.command.impl.brands.BrandsCommand;
import club.nezxenka.netvision.command.impl.falsepositive.FalsePositiveCommand;
import club.nezxenka.netvision.command.impl.help.HelpCommand;
import club.nezxenka.netvision.command.impl.history.HistoryCommand;
import club.nezxenka.netvision.command.impl.logs.LogsCommand;
import club.nezxenka.netvision.command.impl.menu.MenuCommand;
import club.nezxenka.netvision.command.impl.prob.ProbCommand;
import club.nezxenka.netvision.command.impl.profile.ProfileCommand;
import club.nezxenka.netvision.command.impl.punish.PunishCommand;
import club.nezxenka.netvision.command.impl.reload.ReloadCommand;
import club.nezxenka.netvision.command.impl.stats.StatsCommand;
import club.nezxenka.netvision.command.impl.status.StatusCommand;
import club.nezxenka.netvision.command.impl.suspicious.SuspiciousCommand;
import club.nezxenka.netvision.config.core.ConfigManager;
import club.nezxenka.netvision.config.locale.LocaleManager;
import club.nezxenka.netvision.database.connection.DatabaseManager;
import club.nezxenka.netvision.menu.history.HistoryMenu;
import club.nezxenka.netvision.player.manager.PlayerDataManager;
import club.nezxenka.netvision.sender.api.Sender;
import club.nezxenka.netvision.util.message.MessageUtil;
import io.leangen.geantyref.TypeToken;
import java.util.function.Function;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.format.NamedTextColor;
import org.incendo.cloud.exception.InvalidSyntaxException;
import org.incendo.cloud.key.CloudKey;
import org.incendo.cloud.processors.requirements.RequirementApplicable;
import org.incendo.cloud.processors.requirements.RequirementPostprocessor;

public class CommandRegistrationService {

  public static final CloudKey<
          org.incendo.cloud.processors.requirements.Requirements<Sender, SenderRequirement>>
      REQUIREMENT_KEY = CloudKey.of("netvision_requirements", new TypeToken<>() {});
  public static final RequirementApplicable.RequirementApplicableFactory<Sender, SenderRequirement>
      REQUIREMENT_FACTORY = RequirementApplicable.factory(REQUIREMENT_KEY);
  private static boolean commandsRegistered = false;

  public static void registerCommands(
      org.incendo.cloud.CommandManager<Sender> commandManager,
      NetVision plugin,
      AlertManager alertManager,
      DatabaseManager databaseManager,
      ConfigManager configManager,
      LocaleManager localeManager,
      PlayerDataManager playerDataManager,
      HistoryMenu historyMenu) {
    if (commandsRegistered) return;
    for (String root : new String[] {"netvision", "nvp"}) {
      new HelpCommand().register(commandManager, root);
      new AlertsCommand(alertManager).register(commandManager, root);
      new ReloadCommand(plugin).register(commandManager, root);
      new ProbCommand(playerDataManager, localeManager, plugin).register(commandManager, root);
      new ProfileCommand(playerDataManager, localeManager).register(commandManager, root);
      new HistoryCommand(historyMenu).register(commandManager, root);
      new LogsCommand(plugin, databaseManager, configManager, localeManager)
          .register(commandManager, root);
      new PunishCommand(databaseManager).register(commandManager, root);
      new BrandsCommand(alertManager).register(commandManager, root);
      new SuspiciousCommand(playerDataManager, alertManager).register(commandManager, root);
      new StatsCommand(plugin, databaseManager, playerDataManager).register(commandManager, root);
      new MenuCommand(plugin.getChickenCoopMenu()).register(commandManager, root);
      new FalsePositiveCommand(plugin, playerDataManager).register(commandManager, root);
      new StatusCommand(plugin.getHologramManager()).register(commandManager, root);
    }
    new NvpBanCommand(plugin).register(commandManager, "nvp");
    final RequirementPostprocessor<Sender, SenderRequirement> senderRequirementPostprocessor =
        RequirementPostprocessor.of(REQUIREMENT_KEY, new CommandFailureHandler());
    commandManager.registerCommandPostProcessor(senderRequirementPostprocessor);
    registerExceptionHandler(
        commandManager, InvalidSyntaxException.class, e -> MessageUtil.format(e.correctSyntax()));
    commandsRegistered = true;
  }

  private static <E extends Exception> void registerExceptionHandler(
      org.incendo.cloud.CommandManager<Sender> commandManager,
      Class<E> ex,
      Function<E, ComponentLike> toComponent) {
    commandManager
        .exceptionController()
        .registerHandler(
            ex,
            c ->
                c.context()
                    .sender()
                    .sendMessage(
                        toComponent
                            .apply(c.exception())
                            .asComponent()
                            .colorIfAbsent(NamedTextColor.RED)));
  }
}
