package club.nezxenka.netvision.listener.menu.close;

import club.nezxenka.netvision.visual.menu.history.HistoryMenu;
import org.bukkit.event.inventory.InventoryCloseEvent;

public class SessionCleanupHandler {
  private final HistoryMenu historyMenu;

  public SessionCleanupHandler(HistoryMenu historyMenu) {
    this.historyMenu = historyMenu;
  }

  public void cleanup(InventoryCloseEvent event) {
    if (HistoryMenu.isHistoryMenu(event.getView().getTitle()))
      historyMenu.removeSession(event.getPlayer().getUniqueId());
  }
}
