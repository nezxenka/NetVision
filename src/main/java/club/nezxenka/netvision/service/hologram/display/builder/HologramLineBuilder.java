package club.nezxenka.netvision.service.hologram.display.builder;

import java.util.List;

public class HologramLineBuilder {
  public String buildProbabilityLine(List<Double> probs) {
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < probs.size(); i++) {
      double p = probs.get(i);
      sb.append(colorCode(p)).append(String.format("%.2f", p));
      if (i < probs.size() - 1) sb.append("&f ");
    }
    return sb.toString();
  }

  public String buildAverageLine(double avg) {
    return "&7AVG: " + colorCode(avg) + String.format("%.5f", avg);
  }

  private String colorCode(double p) {
    if (p > 0.9) return "&c";
    if (p > 0.5) return "&e";
    return "&a";
  }
}
