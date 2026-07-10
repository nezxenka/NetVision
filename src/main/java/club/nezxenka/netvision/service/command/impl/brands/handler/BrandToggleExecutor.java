package club.nezxenka.netvision.service.command.impl.brands.handler;

import club.nezxenka.netvision.service.signal.internal.SignalManager;
import club.nezxenka.netvision.service.signal.model.SignalType;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class BrandToggleExecutor {
  private final SignalManager alertManager;

  public BrandToggleExecutor(SignalManager alertManager) {
    this.alertManager = alertManager;
  }

  public void toggle(CommandSender sender) {
    if (sender instanceof Player player) alertManager.toggle(player, SignalType.BRAND, false);
    else alertManager.toggleConsoleAlerts(SignalType.BRAND);
  }
}
