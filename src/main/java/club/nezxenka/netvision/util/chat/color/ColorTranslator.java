package club.nezxenka.netvision.util.chat.color;

public class ColorTranslator {
  private static final String COLOR_CODES = "0123456789AaBbCcDdEeFfKkLlMmNnOoRrXx";

  public char[] translate(char altChar, char[] chars) {
    char[] result = new char[chars.length];
    System.arraycopy(chars, 0, result, 0, chars.length);
    for (int i = 0; i < result.length - 1; i++) {
      if (result[i] == altChar && COLOR_CODES.indexOf(result[i + 1]) > -1) {
        result[i] = 167;
        result[i + 1] = Character.toLowerCase(result[i + 1]);
      }
    }
    return result;
  }
}
