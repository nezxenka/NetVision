package club.nezxenka.netvision.player.permission;

import org.bukkit.entity.Player;

public class PlayerPermissionResolver {
  public static boolean hasAlertAutoEnable(Player player) {
    return player.hasPermission("netvision.alerts")
        && player.hasPermission("netvision.alerts.enable-on-join");
  }

  public static boolean hasBrandAutoEnable(Player player) {
    return player.hasPermission("netvision.brand")
        && player.hasPermission("netvision.brand.enable-on-join");
  }

  public static boolean isExempt(Player player) {
    return player.hasPermission("netvision.exempt");
  }
}
