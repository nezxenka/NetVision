package club.nezxenka.netvision.command.impl.suspicious.filter;

import club.nezxenka.netvision.check.packet.ai.internal.AICheck;
import club.nezxenka.netvision.player.model.NetVisionPlayer;
import java.util.List;
import java.util.stream.Collectors;

public class SuspiciousPlayerFilter {
  public List<NetVisionPlayer> filter(List<NetVisionPlayer> players, double minBuffer) {
    return players.stream()
        .filter(
            p -> {
              AICheck c = p.getCheckManager().getCheck(AICheck.class);
              return c != null && c.getBuffer() > minBuffer;
            })
        .collect(Collectors.toList());
  }
}
