package club.nezxenka.netvision.menu.chickencoop;

import club.nezxenka.netvision.NetVision;
import club.nezxenka.netvision.database.model.PlayerMenuData;
import club.nezxenka.netvision.menu.chickencoop.display.WoolItemFactory;
import club.nezxenka.netvision.menu.chickencoop.model.PlayerRiskData;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class ChickenCoopMenu {
  private static final int MENU_SIZE = 54;
  private static final String MENU_TITLE = "Курятник";
  private static final int MAX_PROBABILITIES = 10;
  private final NetVision plugin;
  private final LinkedHashMap<UUID, PlayerRiskData> playerRisks = new LinkedHashMap<>();

  public ChickenCoopMenu(NetVision plugin) {
    this.plugin = plugin;
    loadFromDatabase();
  }

  private void loadFromDatabase() {
    Bukkit.getScheduler()
        .runTaskAsynchronously(
            plugin,
            () -> {
              Map<UUID, PlayerMenuData> data =
                  plugin.getDatabaseManager().getDatabase().getAllOnlinePlayerMenuData();
              Bukkit.getScheduler()
                  .runTask(
                      plugin,
                      () -> {
                        for (PlayerMenuData menuData : data.values()) {
                          PlayerRiskData riskData =
                              new PlayerRiskData(menuData.getUuid(), menuData.getPlayerName());
                          for (Double prob : menuData.getProbabilities())
                            riskData.addProbability(prob);
                          playerRisks.put(menuData.getUuid(), riskData);
                        }
                      });
            });
  }

  public void addOrUpdatePlayer(UUID uuid, String playerName, double probability) {
    PlayerRiskData data = playerRisks.get(uuid);
    if (data == null) {
      data = new PlayerRiskData(uuid, playerName);
      playerRisks.put(uuid, data);
    }
    data.addProbability(probability);
    Bukkit.getScheduler()
        .runTaskAsynchronously(
            plugin,
            () ->
                plugin
                    .getDatabaseManager()
                    .getDatabase()
                    .saveProbability(uuid, playerName, probability));
    if (playerRisks.size() > MENU_SIZE) {
      Iterator<UUID> iterator = playerRisks.keySet().iterator();
      if (iterator.hasNext()) {
        iterator.next();
        iterator.remove();
      }
    }
  }

  public void removePlayer(UUID uuid) {
    playerRisks.remove(uuid);
  }

  public void restorePlayer(UUID uuid, String playerName) {
    if (playerRisks.containsKey(uuid)) return;
    Bukkit.getScheduler()
        .runTaskAsynchronously(
            plugin,
            () -> {
              List<Double> probs =
                  plugin
                      .getDatabaseManager()
                      .getDatabase()
                      .getPlayerProbabilities(uuid, MAX_PROBABILITIES);
              if (!probs.isEmpty())
                Bukkit.getScheduler()
                    .runTask(
                        plugin,
                        () -> {
                          PlayerRiskData data = new PlayerRiskData(uuid, playerName);
                          for (Double prob : probs) data.addProbability(prob);
                          playerRisks.put(uuid, data);
                        });
            });
  }

  public void openMenu(Player viewer) {
    Inventory inventory = Bukkit.createInventory(null, MENU_SIZE, MENU_TITLE);
    java.util.List<PlayerRiskData> sortedPlayers =
        playerRisks.values().stream()
            .filter(data -> Bukkit.getPlayer(data.getUuid()) != null)
            .sorted(
                (p1, p2) -> Double.compare(p2.getAverageProbability(), p1.getAverageProbability()))
            .collect(Collectors.toList());
    int slot = 0;
    for (PlayerRiskData data : sortedPlayers) {
      if (slot >= MENU_SIZE) break;
      inventory.setItem(slot, WoolItemFactory.create(data));
      slot++;
    }
    viewer.openInventory(inventory);
  }

  public UUID getPlayerUuidBySlot(int slot) {
    if (slot < 0 || slot >= MENU_SIZE) return null;
    java.util.List<PlayerRiskData> sortedPlayers =
        playerRisks.values().stream()
            .filter(data -> Bukkit.getPlayer(data.getUuid()) != null)
            .sorted(
                (p1, p2) -> Double.compare(p2.getAverageProbability(), p1.getAverageProbability()))
            .collect(Collectors.toList());
    if (slot >= sortedPlayers.size()) return null;
    return sortedPlayers.get(slot).getUuid();
  }

  public void clear() {
    playerRisks.clear();
  }

  public static boolean isChickenCoopMenu(String title) {
    return MENU_TITLE.equals(title);
  }
}
