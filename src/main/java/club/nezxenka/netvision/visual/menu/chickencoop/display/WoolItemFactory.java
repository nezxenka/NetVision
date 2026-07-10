package club.nezxenka.netvision.visual.menu.coop.display;

import club.nezxenka.netvision.visual.menu.coop.model.PlayerRiskData;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class WoolItemFactory {
  public static ItemStack create(PlayerRiskData data) {
    double avgProbability = data.getAverageProbability();
    Material woolType = getWoolByProbability(avgProbability);
    ItemStack wool = new ItemStack(woolType);
    ItemMeta meta = wool.getItemMeta();
    if (meta != null) {
      meta.setDisplayName(ChatColor.WHITE + data.getPlayerName());
      List<String> lore = new ArrayList<>();
      lore.add("");
      lore.add(ChatColor.GRAY + "Последние проверки:");
      List<Double> probs = data.getLastProbabilities();
      if (probs.isEmpty()) lore.add(ChatColor.GRAY + "Нет данных");
      else if (probs.size() <= 5) lore.add(formatLastProbabilitiesWithColors(probs));
      else {
        lore.add(formatLastProbabilitiesWithColors(probs.subList(0, 5)));
        lore.add(formatLastProbabilitiesWithColors(probs.subList(5, Math.min(10, probs.size()))));
      }
      lore.add("");
      lore.add(ChatColor.GRAY + "Средний риск:");
      ChatColor avgColor = getColorByProbability(avgProbability);
      lore.add(avgColor + "AVG " + String.format("%.2f", avgProbability));
      lore.add("");
      lore.add(ChatColor.GREEN + "Нажмите, чтобы следить");
      meta.setLore(lore);
      wool.setItemMeta(meta);
    }
    return wool;
  }

  private static Material getWoolByProbability(double probability) {
    if (probability > 0.9) return Material.RED_WOOL;
    else if (probability > 0.5) return Material.YELLOW_WOOL;
    else return Material.LIME_WOOL;
  }

  private static ChatColor getColorByProbability(double probability) {
    if (probability > 0.9) return ChatColor.RED;
    else if (probability > 0.5) return ChatColor.YELLOW;
    else return ChatColor.GREEN;
  }

  private static String formatLastProbabilitiesWithColors(List<Double> probabilities) {
    if (probabilities.isEmpty()) return ChatColor.GRAY + "Нет данных";
    StringBuilder result = new StringBuilder();
    for (int i = 0; i < probabilities.size(); i++) {
      double prob = probabilities.get(i);
      ChatColor color = getColorByProbability(prob);
      result.append(color).append(String.format("%.2f", prob));
      if (i < probabilities.size() - 1) result.append(ChatColor.GRAY).append(", ");
    }
    return result.toString();
  }
}
