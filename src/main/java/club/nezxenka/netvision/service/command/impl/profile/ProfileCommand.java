package club.nezxenka.netvision.service.command.impl.profile;

import club.nezxenka.netvision.actor.manager.PlayerDataManager;
import club.nezxenka.netvision.actor.model.NetVisionPlayer;
import club.nezxenka.netvision.audience.api.Sender;
import club.nezxenka.netvision.core.locale.LocaleManager;
import club.nezxenka.netvision.engine.aim.AimEvaluator;
import club.nezxenka.netvision.engine.network.neural.NeuralAnalyzer;
import club.nezxenka.netvision.service.command.api.NetVisionCommand;
import club.nezxenka.netvision.util.message.Message;
import club.nezxenka.netvision.util.message.MessageUtil;
import club.nezxenka.netvision.util.time.TimeUtil;
import org.bukkit.Statistic;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.incendo.cloud.CommandManager;
import org.incendo.cloud.bukkit.parser.PlayerParser;
import org.incendo.cloud.context.CommandContext;

public class ProfileCommand implements NetVisionCommand {

  private final PlayerDataManager playerDataManager;
  private final LocaleManager localeManager;

  public ProfileCommand(PlayerDataManager playerDataManager, LocaleManager localeManager) {
    this.playerDataManager = playerDataManager;
    this.localeManager = localeManager;
  }

  @Override
  public void register(CommandManager<Sender> manager, String rootName) {
    manager.command(
        manager
            .commandBuilder(rootName)
            .literal("profile")
            .permission("netvision.profile")
            .required("target", PlayerParser.playerParser())
            .handler(this::execute));
  }

  private void execute(CommandContext<Sender> context) {
    final CommandSender sender = context.sender().getNativeSender();
    final Player target = context.get("target");
    NetVisionPlayer nvPlayer = playerDataManager.getPlayer(target);
    if (nvPlayer == null) {
      MessageUtil.sendMessage(sender, Message.PROFILE_NO_DATA);
      return;
    }
    NeuralAnalyzer aiCheck = nvPlayer.getModuleCoordinator().getModule(NeuralAnalyzer.class);
    AimEvaluator aimProcessor = nvPlayer.getModuleCoordinator().getModule(AimEvaluator.class);
    long sessionMillis = System.currentTimeMillis() - nvPlayer.getJoinTime();
    long totalPlayTicks = 0;
    try {
      totalPlayTicks = target.getStatistic(Statistic.PLAY_ONE_MINUTE);
    } catch (IllegalArgumentException e) {
      totalPlayTicks = 0;
    }
    long totalPlayMillis = totalPlayTicks * 50;
    MessageUtil.sendMessageList(
        sender,
        Message.PROFILE_LINES,
        "player",
        target.getName(),
        "ping",
        String.valueOf(target.getPing()),
        "version",
        nvPlayer.getUser().getClientVersion().getReleaseName(),
        "brand",
        nvPlayer.getBrand(),
        "session_time",
        TimeUtil.formatDuration(sessionMillis, localeManager),
        "total_playtime",
        TimeUtil.formatDuration(totalPlayMillis, localeManager),
        "sens_x",
        aimProcessor != null ? String.format("%.2f", aimProcessor.getSensitivityX() * 200) : "N/A",
        "sens_y",
        aimProcessor != null ? String.format("%.2f", aimProcessor.getSensitivityY() * 200) : "N/A",
        "ai_buffer",
        aiCheck != null ? String.format("%.2f", aiCheck.getBuffer()) : "N/A",
        "ai_probs_90",
        aiCheck != null ? String.valueOf(aiCheck.getProb90()) : "N/A");
  }
}
