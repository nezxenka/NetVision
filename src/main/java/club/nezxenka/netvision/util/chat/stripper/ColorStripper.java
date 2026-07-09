package club.nezxenka.netvision.util.chat.stripper;

import java.util.regex.Pattern;

public class ColorStripper {
  private static final Pattern PATTERN = Pattern.compile("(?i)" + '\u00A7' + "[0-9A-FK-ORX]");

  public String strip(String input) {
    return input == null ? null : PATTERN.matcher(input).replaceAll("");
  }
}
