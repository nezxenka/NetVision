package club.nezxenka.netvision.util.time.ago;

import java.time.Instant;
import java.util.concurrent.TimeUnit;

public class TimeAgoFormatter {

  public String format(
      Instant instant,
      String agoSuffix,
      String dayUnit,
      String hourUnit,
      String minUnit,
      String secUnit) {
    long millis = System.currentTimeMillis() - instant.toEpochMilli();
    long days = TimeUnit.MILLISECONDS.toDays(millis);
    if (days > 0) return days + dayUnit + agoSuffix;
    long hours = TimeUnit.MILLISECONDS.toHours(millis);
    if (hours > 0) return hours + hourUnit + agoSuffix;
    long minutes = TimeUnit.MILLISECONDS.toMinutes(millis);
    if (minutes > 0) return minutes + minUnit + agoSuffix;
    return TimeUnit.MILLISECONDS.toSeconds(millis) + secUnit + agoSuffix;
  }
}
