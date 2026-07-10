package club.nezxenka.netvision.listener.menu.click;

import club.nezxenka.netvision.visual.menu.coop.ChickenCoopMenu;
import club.nezxenka.netvision.visual.menu.history.HistoryMenu;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

public class ClickRouter {

  public ClickRouter(ChickenCoopMenu chickenCoop, HistoryMenu history) {}

  public void route(InventoryClickEvent event, Player player, String title) {
    if (ChickenCoopMenu.isChickenCoopMenu(title)) event.setCancelled(true);
    else if (HistoryMenu.isHistoryMenu(title)) event.setCancelled(true);
  }

  public boolean matchesChickenCoop(String title) {
    return ChickenCoopMenu.isChickenCoopMenu(title);
  }

  public boolean matchesHistory(String title) {
    return HistoryMenu.isHistoryMenu(title);
  }
}
