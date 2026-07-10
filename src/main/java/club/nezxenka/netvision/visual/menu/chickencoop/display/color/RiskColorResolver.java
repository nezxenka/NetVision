package club.nezxenka.netvision.visual.menu.coop.display.color;

import org.bukkit.ChatColor;
import org.bukkit.Material;

public class RiskColorResolver {
  public ChatColor resolveChatColor(double probability) {
    if (probability > 0.9) return ChatColor.RED;
    if (probability > 0.5) return ChatColor.YELLOW;
    return ChatColor.GREEN;
  }

  public Material resolveWool(double probability) {
    if (probability > 0.9) return Material.RED_WOOL;
    if (probability > 0.5) return Material.YELLOW_WOOL;
    return Material.LIME_WOOL;
  }
}
