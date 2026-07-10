package club.nezxenka.netvision.service.command.impl.suspicious.filter;

import club.nezxenka.netvision.actor.model.NetVisionPlayer;
import club.nezxenka.netvision.engine.network.neural.NeuralAnalyzer;
import java.util.List;
import java.util.stream.Collectors;

public class SuspiciousPlayerFilter {
  public List<NetVisionPlayer> filter(List<NetVisionPlayer> players, double minBuffer) {
    return players.stream()
        .filter(
            p -> {
              NeuralAnalyzer c = p.getModuleCoordinator().getModule(NeuralAnalyzer.class);
              return c != null && c.getBuffer() > minBuffer;
            })
        .collect(Collectors.toList());
  }
}
