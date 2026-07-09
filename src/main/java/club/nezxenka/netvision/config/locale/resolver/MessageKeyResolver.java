package club.nezxenka.netvision.config.locale.resolver;

import club.nezxenka.netvision.util.message.Message;
import java.util.List;
import org.bukkit.configuration.file.FileConfiguration;

public class MessageKeyResolver {
  private final FileConfiguration config;

  public MessageKeyResolver(FileConfiguration config) {
    this.config = config;
  }

  public String resolve(Message key) {
    return config.getString(key.getPath(), "Missing: " + key.getPath());
  }

  public List<String> resolveList(Message key) {
    return config.getStringList(key.getPath());
  }
}
