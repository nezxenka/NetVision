package club.nezxenka.netvision.menu.history.display.color;

import org.bukkit.ChatColor;
import org.bukkit.Material;

public class HistoryColorScheme {
  public ChatColor textColor(double probability) {
    if (probability > 0.9) return ChatColor.RED;
    if (probability > 0.5) return ChatColor.YELLOW;
    return ChatColor.GREEN;
  }

  public Material glassType(double probability) {
    if (probability > 0.9) return Material.RED_STAINED_GLASS_PANE;
    if (probability > 0.5) return Material.YELLOW_STAINED_GLASS_PANE;
    return Material.LIME_STAINED_GLASS_PANE;
  }
}
