package club.nezxenka.netvision.command.impl.suspicious.sorter;

import club.nezxenka.netvision.check.packet.ai.internal.AICheck;
import club.nezxenka.netvision.player.model.NetVisionPlayer;
import java.util.Comparator;

public class SuspiciousBufferComparator implements Comparator<NetVisionPlayer> {
  @Override
  public int compare(NetVisionPlayer a, NetVisionPlayer b) {
    AICheck ca = a.getCheckManager().getCheck(AICheck.class);
    AICheck cb = b.getCheckManager().getCheck(AICheck.class);
    double ba = ca != null ? ca.getBuffer() : 0;
    double bb = cb != null ? cb.getBuffer() : 0;
    return Double.compare(bb, ba);
  }
}
