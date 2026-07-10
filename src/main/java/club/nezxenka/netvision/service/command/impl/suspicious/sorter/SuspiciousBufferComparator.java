package club.nezxenka.netvision.service.command.impl.suspicious.sorter;

import club.nezxenka.netvision.actor.model.NetVisionPlayer;
import club.nezxenka.netvision.engine.network.neural.NeuralAnalyzer;
import java.util.Comparator;

public class SuspiciousBufferComparator implements Comparator<NetVisionPlayer> {
  @Override
  public int compare(NetVisionPlayer a, NetVisionPlayer b) {
    NeuralAnalyzer ca = a.getModuleCoordinator().getModule(NeuralAnalyzer.class);
    NeuralAnalyzer cb = b.getModuleCoordinator().getModule(NeuralAnalyzer.class);
    double ba = ca != null ? ca.getBuffer() : 0;
    double bb = cb != null ? cb.getBuffer() : 0;
    return Double.compare(bb, ba);
  }
}
