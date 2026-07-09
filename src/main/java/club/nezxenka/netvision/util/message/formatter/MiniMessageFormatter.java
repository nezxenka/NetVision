package club.nezxenka.netvision.util.message.formatter;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;

public class MiniMessageFormatter {
  private static final MiniMessage MINI = MiniMessage.miniMessage();

  public Component format(String template, String... placeholders) {
    TagResolver.Builder builder = TagResolver.builder();
    if (placeholders.length > 0 && placeholders.length % 2 == 0) {
      for (int i = 0; i < placeholders.length; i += 2)
        builder.resolver(
            Placeholder.component(placeholders[i], Component.text(placeholders[i + 1])));
    }
    return MINI.deserialize(template, builder.build());
  }
}
