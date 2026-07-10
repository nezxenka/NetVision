package club.nezxenka.netvision.service.command.impl.status;

import club.nezxenka.netvision.audience.api.Sender;
import club.nezxenka.netvision.service.command.api.NetVisionCommand;
import club.nezxenka.netvision.service.hologram.internal.HologramManager;
import club.nezxenka.netvision.util.message.Message;
import club.nezxenka.netvision.util.message.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.incendo.cloud.CommandManager;
import org.incendo.cloud.bukkit.parser.PlayerParser;
import org.incendo.cloud.context.CommandContext;

public class StatusCommand implements NetVisionCommand {
  private final HologramManager hologramManager;

  public StatusCommand(HologramManager hologramManager) {
    this.hologramManager = hologramManager;
  }

  @Override
  public void register(CommandManager<Sender> manager, String rootName) {
    manager.command(
        manager
            .commandBuilder(rootName)
            .literal("status")
            .permission("netvision.status")
            .handler(this::executeAll));
    manager.command(
        manager
            .commandBuilder(rootName)
            .literal("status")
            .permission("netvision.status")
            .required("target", PlayerParser.playerParser())
            .handler(this::executeTarget));
  }

  private void executeAll(CommandContext<Sender> context) {
    CommandSender sender = context.sender().getNativeSender();
    if (!(sender instanceof Player player)) {
      MessageUtil.sendMessage(sender, Message.RUN_AS_PLAYER);
      return;
    }
    boolean hasAny = false;
    for (Player target : Bukkit.getOnlinePlayers()) {
      if (hologramManager.isEnabled(player, target)) {
        hasAny = true;
        break;
      }
    }
    if (hasAny) {
      hologramManager.disableForAll(player);
      player.sendMessage(ChatColor.YELLOW + "Голограммы отключены для всех игроков.");
    } else {
      hologramManager.enableForAll(player);
      player.sendMessage(ChatColor.GREEN + "Голограммы включены для всех игроков.");
    }
  }

  private void executeTarget(CommandContext<Sender> context) {
    CommandSender sender = context.sender().getNativeSender();
    if (!(sender instanceof Player player)) {
      MessageUtil.sendMessage(sender, Message.RUN_AS_PLAYER);
      return;
    }
    Player target = context.get("target");
    if (target.hasPermission("netvision.exempt")) {
      player.sendMessage(ChatColor.RED + "Этот игрок освобожден от проверок.");
      return;
    }
    if (hologramManager.isEnabled(player, target)) {
      hologramManager.disableHologram(player, target);
      player.sendMessage(
          ChatColor.YELLOW
              + "Голограмма отключена для игрока "
              + ChatColor.WHITE
              + target.getName());
    } else {
      hologramManager.enableHologram(player, target);
      player.sendMessage(
          ChatColor.GREEN + "Голограмма включена для игрока " + ChatColor.WHITE + target.getName());
    }
  }
}
