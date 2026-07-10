package club.nezxenka.netvision.event.listener;

import club.nezxenka.netvision.actor.manager.PlayerDataManager;
import club.nezxenka.netvision.actor.model.NetVisionPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class DamageEventListener implements Listener {
  private final PlayerDataManager playerDataManager;

  public DamageEventListener(PlayerDataManager playerDataManager) {
    this.playerDataManager = playerDataManager;
  }

  @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
  public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
    if (!(event.getDamager() instanceof Player damager)) return;
    NetVisionPlayer nvPlayer = playerDataManager.getPlayer(damager);
    if (nvPlayer == null) return;
    double multiplier = nvPlayer.getDmgMultiplier();
    if (multiplier < 1.0) event.setDamage(event.getDamage() * multiplier);
  }
}
