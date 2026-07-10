package club.nezxenka.netvision.util.message;

import club.nezxenka.netvision.core.locale.LocaleManager;
import java.util.List;
import java.util.stream.Collectors;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.command.CommandSender;

public class MessageUtil {
  private static final MiniMessage miniMessage = MiniMessage.miniMessage();
  private static LocaleManager localeManager;
  private static BukkitAudiences adventure;

  public static void init(LocaleManager localeManager, BukkitAudiences adventure) {
    MessageUtil.localeManager = localeManager;
    MessageUtil.adventure = adventure;
  }

  public static Component format(String message, String... placeholders) {
    String processedMessage =
        message.replace("<prefix>", localeManager.getRawMessage(Message.PREFIX));
    TagResolver.Builder resolverBuilder = TagResolver.builder();
    if (placeholders.length > 0) {
      if (placeholders.length % 2 != 0)
        System.err.println("Invalid placeholders count for message: " + message);
      else
        for (int i = 0; i < placeholders.length; i += 2)
          resolverBuilder.resolver(
              Placeholder.component(placeholders[i], Component.text(placeholders[i + 1])));
    }
    return miniMessage.deserialize(processedMessage, resolverBuilder.build());
  }

  public static void sendMessage(CommandSender sender, Message key, String... placeholders) {
    adventure.sender(sender).sendMessage(getMessage(key, placeholders));
  }

  public static void sendMessageList(CommandSender sender, Message key, String... placeholders) {
    getMessageList(key, placeholders).forEach(line -> adventure.sender(sender).sendMessage(line));
  }

  public static Component getMessage(Message key, String... placeholders) {
    String rawMessage = localeManager.getRawMessage(key);
    return format(rawMessage, placeholders);
  }

  public static List<Component> getMessageList(Message key, String... placeholders) {
    return localeManager.getRawMessageList(key).stream()
        .map(line -> format(line, placeholders))
        .collect(Collectors.toList());
  }
}
