package club.nezxenka.netvision.service.command.impl.falsepositive;

import club.nezxenka.netvision.NetVision;
import club.nezxenka.netvision.actor.manager.PlayerDataManager;
import club.nezxenka.netvision.actor.model.NetVisionPlayer;
import club.nezxenka.netvision.audience.api.Sender;
import club.nezxenka.netvision.engine.model.TickSample;
import club.nezxenka.netvision.engine.network.neural.NeuralAnalyzer;
import club.nezxenka.netvision.remote.restore.DataRestorer;
import club.nezxenka.netvision.service.command.api.NetVisionCommand;
import club.nezxenka.netvision.util.message.Message;
import club.nezxenka.netvision.util.message.MessageUtil;
import org.bukkit.entity.Player;
import org.incendo.cloud.CommandManager;
import org.incendo.cloud.context.CommandContext;

public class FalsePositiveCommand implements NetVisionCommand {

  private final PlayerDataManager playerDataManager;
  private final DataRestorer dataRestorer;

  public FalsePositiveCommand(NetVision plugin, PlayerDataManager playerDataManager) {
    this.playerDataManager = playerDataManager;
    this.dataRestorer = new DataRestorer(plugin);
  }

  @Override
  public void register(CommandManager<Sender> manager, String rootName) {
    manager.command(
        manager
            .commandBuilder(rootName)
            .literal("falsepositive", "fp")
            .permission("netvision.falsepositive")
            .literal("restore")
            .required("target", org.incendo.cloud.bukkit.parser.PlayerParser.playerParser())
            .handler(this::handleFalsePositive));
  }

  private void handleFalsePositive(CommandContext<Sender> context) {
    Sender sender = context.sender();
    Player target = context.get("target");
    NetVisionPlayer nvPlayer = playerDataManager.getPlayer(target);
    if (nvPlayer == null) {
      MessageUtil.sendMessage(sender.getNativeSender(), Message.FP_NO_DATA);
      return;
    }
    NeuralAnalyzer aiCheck = nvPlayer.getModuleCoordinator().getModule(NeuralAnalyzer.class);
    if (aiCheck == null) {
      MessageUtil.sendMessage(sender.getNativeSender(), Message.FP_NO_DATA);
      return;
    }
    java.util.List<TickSample> history = aiCheck.getTickHistory();
    if (history.isEmpty()) {
      MessageUtil.sendMessage(sender.getNativeSender(), Message.FP_NO_DATA);
      return;
    }
    boolean success = dataRestorer.restoreData(target.getName(), history);
    if (success)
      MessageUtil.sendMessage(
          sender.getNativeSender(), Message.FP_SUCCESS, "player", target.getName());
    else MessageUtil.sendMessage(sender.getNativeSender(), Message.FP_FAIL);
  }
}
