package club.nezxenka.netvision.core.locale;

import club.nezxenka.netvision.NetVision;
import club.nezxenka.netvision.core.config.ConfigManager;
import club.nezxenka.netvision.util.message.Message;
import java.io.File;
import java.util.List;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class LocaleManager {

  private final NetVision plugin;
  private final ConfigManager configManager;
  private FileConfiguration messagesConfig;

  public LocaleManager(NetVision plugin, ConfigManager configManager) {
    this.plugin = plugin;
    this.configManager = configManager;
    reload();
  }

  public void reload() {
    String locale = configManager.getConfig().getString("locale", "en");
    File messagesDir = new File(plugin.getDataFolder(), "messages");
    if (!messagesDir.exists()) {
      messagesDir.mkdirs();
    }
    saveDefaultLocale("en");
    if (!locale.equalsIgnoreCase("en")) {
      saveDefaultLocale(locale);
    }
    File messagesFile = new File(messagesDir, "messages_" + locale + ".yml");
    if (!messagesFile.exists()) {
      plugin.getLogger().warning("Locale " + locale + " not found.");
      messagesFile = new File(messagesDir, "messages_en.yml");
    }
    this.messagesConfig = YamlConfiguration.loadConfiguration(messagesFile);
    File defaultFile = new File(messagesDir, "messages_en.yml");
    if (defaultFile.exists()) {
      this.messagesConfig.setDefaults(YamlConfiguration.loadConfiguration(defaultFile));
    }
  }

  private void saveDefaultLocale(String locale) {
    File dir = new File(plugin.getDataFolder(), "messages");
    File file = new File(dir, "messages_" + locale + ".yml");
    if (!file.exists()) {
      plugin.saveResource("messages/messages_" + locale + ".yml", false);
    }
  }

  public String getRawMessage(Message key) {
    return messagesConfig.getString(key.getPath(), "Missing message: " + key.getPath());
  }

  public List<String> getRawMessageList(Message key) {
    return messagesConfig.getStringList(key.getPath());
  }
}
