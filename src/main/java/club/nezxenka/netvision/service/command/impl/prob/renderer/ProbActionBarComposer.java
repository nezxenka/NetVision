package club.nezxenka.netvision.service.command.impl.prob.renderer;

import club.nezxenka.netvision.core.locale.LocaleManager;
import club.nezxenka.netvision.engine.network.neural.NeuralAnalyzer;
import club.nezxenka.netvision.util.message.Message;
import java.util.Locale;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.entity.Player;

public class ProbActionBarComposer {
  private final LocaleManager localeManager;

  public ProbActionBarComposer(LocaleManager localeManager) {
    this.localeManager = localeManager;
  }

  public Component compose(NeuralAnalyzer check, Player target) {
    double probability = check.getLastProbability();
    double buffer = check.getBuffer();
    int ping = target.getPing();
    TextColor probColor =
        probability > 0.9
            ? NamedTextColor.RED
            : (probability > 0.5 ? NamedTextColor.YELLOW : NamedTextColor.GREEN);
    TextColor vlColor =
        buffer > 30
            ? NamedTextColor.DARK_RED
            : (buffer > 15 ? NamedTextColor.RED : NamedTextColor.GREEN);
    TextColor pingColor =
        ping > 150
            ? NamedTextColor.RED
            : (ping > 80 ? NamedTextColor.YELLOW : NamedTextColor.GREEN);
    TextComponent bufferComp = Component.text(String.format(Locale.US, "%.2f", buffer), vlColor);
    if (buffer > 30) bufferComp = bufferComp.decorate(TextDecoration.BOLD);
    return Component.text()
        .append(
            Component.text(localeManager.getRawMessage(Message.PROB_FORMAT_LABEL_PROB))
                .color(probColor))
        .append(Component.text(" (").color(probColor))
        .append(Component.text(target.getName(), probColor))
        .append(Component.text("): ").color(probColor))
        .append(Component.text(String.format(Locale.US, "%.4f", probability), probColor))
        .append(
            Component.text(
                localeManager.getRawMessage(Message.PROB_FORMAT_SEPARATOR),
                NamedTextColor.DARK_GRAY))
        .append(
            Component.text(localeManager.getRawMessage(Message.PROB_FORMAT_LABEL_BUFFER))
                .color(vlColor))
        .append(Component.text(": ").color(vlColor))
        .append(bufferComp)
        .append(
            Component.text(
                localeManager.getRawMessage(Message.PROB_FORMAT_SEPARATOR),
                NamedTextColor.DARK_GRAY))
        .append(
            Component.text(localeManager.getRawMessage(Message.PROB_FORMAT_LABEL_PING))
                .color(pingColor))
        .append(Component.text(": ").color(pingColor))
        .append(Component.text(ping, pingColor))
        .append(
            Component.text(localeManager.getRawMessage(Message.PROB_FORMAT_SUFFIX_PING))
                .color(pingColor))
        .build();
  }
}
