package club.nezxenka.netvision.util.time.duration;

import java.util.concurrent.TimeUnit;

public class DurationFormatter {
  public String format(
      long millis, String dayUnit, String hourUnit, String minUnit, String secUnit) {
    if (millis < 0) return "0" + secUnit;
    long days = TimeUnit.MILLISECONDS.toDays(millis);
    millis -= TimeUnit.DAYS.toMillis(days);
    long hours = TimeUnit.MILLISECONDS.toHours(millis);
    millis -= TimeUnit.HOURS.toMillis(hours);
    long minutes = TimeUnit.MILLISECONDS.toMinutes(millis);
    millis -= TimeUnit.MINUTES.toMillis(minutes);
    long seconds = TimeUnit.MILLISECONDS.toSeconds(millis);
    StringBuilder sb = new StringBuilder();
    if (days > 0) sb.append(days).append(dayUnit).append(" ");
    if (hours > 0) sb.append(hours).append(hourUnit).append(" ");
    if (minutes > 0) sb.append(minutes).append(minUnit).append(" ");
    if (sb.length() == 0 || seconds > 0) sb.append(seconds).append(secUnit);
    return sb.toString().trim();
  }
}
