package club.nezxenka.netvision.service.hologram.display.formatter;

public class HologramColorFormatter {
  public String format(double probability) {
    if (probability > 0.9) return "&c";
    if (probability > 0.5) return "&e";
    return "&a";
  }

  public String formatAverage(double avg) {
    return "&7AVG: " + format(avg) + String.format("%.5f", avg);
  }
}
