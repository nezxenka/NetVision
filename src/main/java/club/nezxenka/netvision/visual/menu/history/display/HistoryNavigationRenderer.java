package club.nezxenka.netvision.visual.menu.history.display;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class HistoryNavigationRenderer {
  private static final String PREV_ARROW_NAME = ChatColor.GREEN + "← Предыдущая страница";
  private static final String NEXT_ARROW_NAME = ChatColor.GREEN + "Следующая страница →";

  public static void fillNavigation(Inventory inv, int currentPage, int totalPages) {
    if (currentPage == 1) {
      for (int i = 45; i <= 52; i++) inv.setItem(i, createGlassPane());
      inv.setItem(53, totalPages > 1 ? createArrowItem(false) : createGlassPane());
    } else if (currentPage == totalPages) {
      inv.setItem(45, createArrowItem(true));
      for (int i = 46; i <= 53; i++) inv.setItem(i, createGlassPane());
    } else {
      inv.setItem(45, createArrowItem(true));
      for (int i = 46; i <= 52; i++) inv.setItem(i, createGlassPane());
      inv.setItem(53, createArrowItem(false));
    }
  }

  private static ItemStack createGlassPane() {
    ItemStack pane = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
    ItemMeta meta = pane.getItemMeta();
    if (meta != null) {
      meta.setDisplayName(" ");
      pane.setItemMeta(meta);
    }
    return pane;
  }

  private static ItemStack createArrowItem(boolean previous) {
    ItemStack item = new ItemStack(Material.ARROW);
    ItemMeta meta = item.getItemMeta();
    if (meta != null) {
      meta.setDisplayName(previous ? PREV_ARROW_NAME : NEXT_ARROW_NAME);
      item.setItemMeta(meta);
    }
    return item;
  }
}
