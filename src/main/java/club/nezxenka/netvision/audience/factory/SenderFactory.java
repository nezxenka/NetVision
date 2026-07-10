package club.nezxenka.netvision.audience.factory;

import club.nezxenka.netvision.NetVision;
import club.nezxenka.netvision.audience.api.Sender;
import java.util.UUID;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.incendo.cloud.SenderMapper;
import org.jetbrains.annotations.NotNull;

public class SenderFactory implements SenderMapper<CommandSender, Sender> {

  private final NetVision plugin;

  public SenderFactory(NetVision plugin) {
    this.plugin = plugin;
  }

  @Override
  public Sender map(@NotNull CommandSender base) {
    if (base instanceof Player player) return new PlayerSender(player, plugin);
    return new ConsoleSender(base, plugin);
  }

  @Override
  public CommandSender reverse(@NotNull Sender mapped) {
    return mapped.getNativeSender();
  }

  private static class PlayerSender implements Sender {

    private final Player player;
    private final NetVision plugin;

    PlayerSender(Player player, NetVision plugin) {
      this.player = player;
      this.plugin = plugin;
    }

    @Override
    public String getName() {
      return player.getName();
    }

    @Override
    public UUID getUniqueId() {
      return player.getUniqueId();
    }

    @Override
    public void sendMessage(String message) {
      player.sendMessage(message);
    }

    @Override
    public void sendMessage(Component message) {
      plugin.getAdventure().player(player).sendMessage(message);
    }

    @Override
    public boolean hasPermission(String permission) {
      return player.hasPermission(permission);
    }

    @Override
    public boolean isConsole() {
      return false;
    }

    @Override
    public boolean isPlayer() {
      return true;
    }

    @Override
    public CommandSender getNativeSender() {
      return player;
    }

    @Override
    public Player getPlayer() {
      return player;
    }
  }

  private static class ConsoleSender implements Sender {

    private final CommandSender sender;
    private final NetVision plugin;

    ConsoleSender(CommandSender sender, NetVision plugin) {
      this.sender = sender;
      this.plugin = plugin;
    }

    @Override
    public String getName() {
      return CONSOLE_NAME;
    }

    @Override
    public UUID getUniqueId() {
      return CONSOLE_UUID;
    }

    @Override
    public void sendMessage(String message) {
      sender.sendMessage(message);
    }

    @Override
    public void sendMessage(Component message) {
      plugin.getAdventure().sender(sender).sendMessage(message);
    }

    @Override
    public boolean hasPermission(String permission) {
      return sender.hasPermission(permission);
    }

    @Override
    public boolean isConsole() {
      return true;
    }

    @Override
    public boolean isPlayer() {
      return false;
    }

    @Override
    public CommandSender getNativeSender() {
      return sender;
    }

    @Override
    public Player getPlayer() {
      return null;
    }
  }
}
