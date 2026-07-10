package club.nezxenka.netvision.service.command.impl.profile.gatherer;

import club.nezxenka.netvision.actor.model.NetVisionPlayer;
import club.nezxenka.netvision.engine.aim.AimEvaluator;
import club.nezxenka.netvision.engine.network.neural.NeuralAnalyzer;
import org.bukkit.Statistic;
import org.bukkit.entity.Player;

public class PlayerStatGatherer {

  public record ProfileStats(
      String playerName,
      int ping,
      String version,
      String brand,
      long sessionMillis,
      long totalPlayMillis,
      String sensX,
      String sensY,
      String aiBuffer,
      String prob90) {}

  public ProfileStats gather(NetVisionPlayer nvPlayer, Player target) {
    AimEvaluator aim = nvPlayer.getModuleCoordinator().getModule(AimEvaluator.class);
    NeuralAnalyzer ai = nvPlayer.getModuleCoordinator().getModule(NeuralAnalyzer.class);
    long session = System.currentTimeMillis() - nvPlayer.getJoinTime();
    long totalTicks = 0;
    try {
      totalTicks = target.getStatistic(Statistic.PLAY_ONE_MINUTE);
    } catch (IllegalArgumentException e) {
      totalTicks = 0;
    }
    return new ProfileStats(
        target.getName(),
        target.getPing(),
        nvPlayer.getUser().getClientVersion().getReleaseName(),
        nvPlayer.getBrand(),
        session,
        totalTicks * 50L,
        aim != null ? String.format("%.2f", aim.getSensitivityX() * 200) : "N/A",
        aim != null ? String.format("%.2f", aim.getSensitivityY() * 200) : "N/A",
        ai != null ? String.format("%.2f", ai.getBuffer()) : "N/A",
        ai != null ? String.valueOf(ai.getProb90()) : "N/A");
  }
}
