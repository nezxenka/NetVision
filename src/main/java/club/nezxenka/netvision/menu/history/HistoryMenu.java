package club.nezxenka.netvision.menu.history;

import club.nezxenka.netvision.NetVision;
import club.nezxenka.netvision.database.api.ViolationDatabase;
import club.nezxenka.netvision.database.model.ProbabilityEntry;
import club.nezxenka.netvision.menu.history.display.HistoryNavigationRenderer;
import club.nezxenka.netvision.menu.history.display.HistoryPaneFactory;
import club.nezxenka.netvision.menu.history.model.HistorySession;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class HistoryMenu {
  private static final int MENU_SIZE = 54;
  private static final int CONTENT_SLOTS = 45;
  private static final int ENTRIES_PER_PAGE = CONTENT_SLOTS;
  private static final int PROBS_PER_ENTRY = 10;
  private static final int MAX_PAGES = 3;
  private static final String MENU_PREFIX = ChatColor.DARK_GRAY + "История ";
  private final NetVision plugin;
  private final Map<UUID, HistorySession> activeSessions = new ConcurrentHashMap<>();

  public HistoryMenu(NetVision plugin) {
    this.plugin = plugin;
  }

  public void open(Player viewer, String targetName, UUID targetUuid, int page) {
    ViolationDatabase db = plugin.getDatabaseManager().getDatabase();
    Bukkit.getScheduler()
        .runTaskAsynchronously(
            plugin,
            () -> {
              int totalProbs = db.getPlayerProbabilityCount(targetUuid);
              int maxEntries = MAX_PAGES * ENTRIES_PER_PAGE;
              int maxProbs = maxEntries * PROBS_PER_ENTRY;
              int probsToLoad = Math.min(totalProbs, maxProbs);
              List<List<ProbabilityEntry>> batches = new ArrayList<>();
              int loaded = 0;
              while (loaded < probsToLoad) {
                List<ProbabilityEntry> entries =
                    db.getPlayerProbabilityEntries(targetUuid, PROBS_PER_ENTRY, loaded);
                if (entries.isEmpty()) break;
                batches.add(entries);
                loaded += entries.size();
                if (entries.size() < PROBS_PER_ENTRY) break;
              }
              int computedTotalPages =
                  Math.max(1, (int) Math.ceil((double) batches.size() / ENTRIES_PER_PAGE));
              if (computedTotalPages > MAX_PAGES) computedTotalPages = MAX_PAGES;
              int clampedPage = page;
              if (clampedPage < 1) clampedPage = 1;
              if (clampedPage > computedTotalPages) clampedPage = computedTotalPages;
              final int currentPage = clampedPage;
              final int finalTotalPages = computedTotalPages;
              Bukkit.getScheduler()
                  .runTask(
                      plugin,
                      () -> {
                        activeSessions.put(
                            viewer.getUniqueId(),
                            new HistorySession(targetUuid, targetName, currentPage));
                        Inventory inv =
                            createInventory(targetName, currentPage, finalTotalPages, batches);
                        viewer.openInventory(inv);
                      });
            });
  }

  private Inventory createInventory(
      String targetName, int currentPage, int totalPages, List<List<ProbabilityEntry>> allBatches) {
    String title = MENU_PREFIX + ChatColor.DARK_GRAY + targetName;
    Inventory inv = Bukkit.createInventory(null, MENU_SIZE, title);
    int startIdx = (currentPage - 1) * ENTRIES_PER_PAGE;
    int endIdx = Math.min(startIdx + ENTRIES_PER_PAGE, allBatches.size());
    int slot = 0;
    for (int i = startIdx; i < endIdx; i++) {
      inv.setItem(slot, HistoryPaneFactory.create(allBatches.get(i)));
      slot++;
    }
    HistoryNavigationRenderer.fillNavigation(inv, currentPage, totalPages);
    return inv;
  }

  public boolean handleClick(Player viewer, int rawSlot) {
    HistorySession session = activeSessions.get(viewer.getUniqueId());
    if (session == null) return false;
    if (rawSlot < 0 || rawSlot >= MENU_SIZE) return false;
    if (rawSlot >= 45 && rawSlot <= 53) {
      int newPage = session.currentPage();
      if (rawSlot == 45) {
        if (session.currentPage() > 1) newPage = session.currentPage() - 1;
      } else if (rawSlot == 53) newPage = session.currentPage() + 1;
      if (newPage != session.currentPage()) {
        viewer.closeInventory();
        open(viewer, session.targetName(), session.targetUuid(), newPage);
        return true;
      }
    }
    return false;
  }

  public void removeSession(UUID viewerUuid) {
    activeSessions.remove(viewerUuid);
  }

  public static boolean isHistoryMenu(String title) {
    return title != null && title.startsWith(MENU_PREFIX);
  }
}
