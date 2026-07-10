package club.nezxenka.netvision.visual.menu.history.display;

import club.nezxenka.netvision.core.storage.model.ProbabilityEntry;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class HistoryPaneFactory {
  public static ItemStack create(List<ProbabilityEntry> entries) {
    double avg = entries.stream().mapToDouble(ProbabilityEntry::probability).average().orElse(0.0);
    ChatColor avgColor = getColorByProbability(avg);
    ItemStack glass = new ItemStack(getGlassByProbability(avg));
    ItemMeta meta = glass.getItemMeta();
    if (meta != null) {
      meta.setDisplayName(avgColor + "AVG: " + String.format("%.4f", avg));
      List<String> lore = new ArrayList<>();
      lore.add("");
      lore.add(
          ChatColor.WHITE
              + "Вер. "
              + ChatColor.DARK_GRAY
              + "   |"
              + ChatColor.WHITE
              + " Сервер"
              + ChatColor.DARK_GRAY
              + "    |"
              + ChatColor.WHITE
              + " Время");
      lore.add(ChatColor.DARK_GRAY + "──────────────────");
      for (ProbabilityEntry entry : entries) {
        ChatColor probColor = getColorByProbability(entry.probability());
        long elapsed = System.currentTimeMillis() - entry.createdAt();
        String timeStr = formatElapsed(elapsed);
        String serverName = entry.server();
        lore.add(
            probColor
                + String.format("%.4f", entry.probability())
                + " "
                + ChatColor.GRAY
                + "|"
                + ChatColor.GRAY
                + " "
                + ChatColor.GRAY
                + serverName
                + " "
                + ChatColor.GRAY
                + "|"
                + ChatColor.GRAY
                + " "
                + timeStr);
      }
      meta.setLore(lore);
      glass.setItemMeta(meta);
    }
    return glass;
  }

  private static Material getGlassByProbability(double probability) {
    if (probability > 0.9) return Material.RED_STAINED_GLASS_PANE;
    if (probability > 0.5) return Material.YELLOW_STAINED_GLASS_PANE;
    return Material.LIME_STAINED_GLASS_PANE;
  }

  private static ChatColor getColorByProbability(double probability) {
    if (probability > 0.9) return ChatColor.RED;
    if (probability > 0.5) return ChatColor.YELLOW;
    return ChatColor.GREEN;
  }

  private static String formatElapsed(long millis) {
    if (millis < 0) return "0ч. 0м.";
    long hours = TimeUnit.MILLISECONDS.toHours(millis);
    long minutes = TimeUnit.MILLISECONDS.toMinutes(millis) % 60;
    return hours + "ч. " + minutes + "м.";
  }
}
