package club.nezxenka.netvision.service.command.impl.logs.parser;

import java.util.concurrent.TimeUnit;

public class TimeFlagDecoder {

  public long decode(String timeArg) {
    if (timeArg == null) return 0L;
    try {
      if (timeArg.length() < 2) return -1L;
      long value = Long.parseLong(timeArg.substring(0, timeArg.length() - 1));
      char unit = Character.toLowerCase(timeArg.charAt(timeArg.length() - 1));
      long multiplier =
          switch (unit) {
            case 'm' -> TimeUnit.MINUTES.toMillis(1);
            case 'h' -> TimeUnit.HOURS.toMillis(1);
            case 'd' -> TimeUnit.DAYS.toMillis(1);
            default -> -1L;
          };
      if (multiplier == -1L) return -1L;
      return System.currentTimeMillis() - value * multiplier;
    } catch (NumberFormatException e) {
      return -1L;
    }
  }

  public boolean isValid(String timeArg) {
    return decode(timeArg) != -1L;
  }
}
