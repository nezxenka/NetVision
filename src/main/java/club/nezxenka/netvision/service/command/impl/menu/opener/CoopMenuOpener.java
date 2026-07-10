package club.nezxenka.netvision.service.command.impl.menu.opener;

import club.nezxenka.netvision.visual.menu.coop.ChickenCoopMenu;
import org.bukkit.entity.Player;

public class CoopMenuOpener {
  private final ChickenCoopMenu menu;

  public CoopMenuOpener(ChickenCoopMenu menu) {
    this.menu = menu;
  }

  public void open(Player player) {
    menu.openMenu(player);
  }
}
