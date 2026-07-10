package club.nezxenka.netvision.listener.menu;

import club.nezxenka.netvision.NetVision;
import club.nezxenka.netvision.visual.menu.coop.ChickenCoopMenu;
import club.nezxenka.netvision.visual.menu.history.HistoryMenu;
import java.util.UUID;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;

public class MenuClickListener implements Listener {
  private final NetVision plugin;
  private final ChickenCoopMenu chickenCoopMenu;
  private final HistoryMenu historyMenu;

  public MenuClickListener(
      NetVision plugin, ChickenCoopMenu chickenCoopMenu, HistoryMenu historyMenu) {
    this.plugin = plugin;
    this.chickenCoopMenu = chickenCoopMenu;
    this.historyMenu = historyMenu;
  }

  @EventHandler
  public void onInventoryClick(InventoryClickEvent event) {
    if (!(event.getWhoClicked() instanceof Player player)) return;
    String title = event.getView().getTitle();
    if (ChickenCoopMenu.isChickenCoopMenu(title)) handleChickenCoopClick(event, player);
    else if (HistoryMenu.isHistoryMenu(title)) handleHistoryClick(event, player);
  }

  @EventHandler
  public void onInventoryClose(InventoryCloseEvent event) {
    if (!(event.getPlayer() instanceof Player)) return;
    String title = event.getView().getTitle();
    if (HistoryMenu.isHistoryMenu(title))
      historyMenu.removeSession(event.getPlayer().getUniqueId());
  }

  private void handleChickenCoopClick(InventoryClickEvent event, Player player) {
    event.setCancelled(true);
    if (event.getCurrentItem() == null || event.getCurrentItem().getType().isAir()) return;
    int slot = event.getRawSlot();
    UUID targetUuid = chickenCoopMenu.getPlayerUuidBySlot(slot);
    if (targetUuid == null) return;
    Player target = plugin.getServer().getPlayer(targetUuid);
    if (target == null || !target.isOnline()) {
      player.sendMessage(ChatColor.RED + "Игрок не в сети!");
      return;
    }
    if (player.getGameMode() != GameMode.SPECTATOR) player.setGameMode(GameMode.SPECTATOR);
    player.teleport(target.getLocation());
    player.sendMessage(
        ChatColor.GREEN + "Вы следите за игроком " + ChatColor.WHITE + target.getName());
    player.closeInventory();
  }

  private void handleHistoryClick(InventoryClickEvent event, Player player) {
    event.setCancelled(true);
    if (event.getCurrentItem() == null || event.getCurrentItem().getType().isAir()) return;
    int slot = event.getRawSlot();
    historyMenu.handleClick(player, slot);
  }
}
