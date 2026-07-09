package club.nezxenka.netvision.util.message.sender;

import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;

public class ComponentSender {
  private final BukkitAudiences adventure;

  public ComponentSender(BukkitAudiences adventure) {
    this.adventure = adventure;
  }

  public void send(CommandSender recipient, Component message) {
    adventure.sender(recipient).sendMessage(message);
  }
}
