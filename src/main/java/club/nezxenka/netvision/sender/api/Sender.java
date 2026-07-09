package club.nezxenka.netvision.sender.api;

import java.util.UUID;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public interface Sender {
  UUID CONSOLE_UUID = new UUID(0, 0);
  String CONSOLE_NAME = "Console";

  String getName();

  UUID getUniqueId();

  void sendMessage(String message);

  void sendMessage(Component message);

  boolean hasPermission(String permission);

  boolean isConsole();

  boolean isPlayer();

  CommandSender getNativeSender();

  Player getPlayer();
}
