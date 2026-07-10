package club.nezxenka.netvision.core.locale.loader;

import java.io.File;
import org.bukkit.plugin.java.JavaPlugin;

public class LocaleFileLoader {
  private final JavaPlugin plugin;

  public LocaleFileLoader(JavaPlugin plugin) {
    this.plugin = plugin;
  }

  public File resolveFile(String locale, File messagesDir) {
    File file = new File(messagesDir, "messages_" + locale + ".yml");
    if (!file.exists()) return new File(messagesDir, "messages_en.yml");
    return file;
  }

  public void ensureDirectory(File dir) {
    if (!dir.exists()) dir.mkdirs();
  }

  public void saveIfMissing(String resourcePath) {
    File file = new File(plugin.getDataFolder(), resourcePath);
    if (!file.exists()) plugin.saveResource(resourcePath, false);
  }
}
