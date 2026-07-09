package club.nezxenka.netvision.command.impl.menu.opener;

import club.nezxenka.netvision.menu.chickencoop.ChickenCoopMenu;
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
