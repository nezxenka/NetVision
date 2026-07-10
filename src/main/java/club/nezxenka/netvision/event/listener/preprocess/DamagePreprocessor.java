package club.nezxenka.netvision.event.listener.preprocess;

import club.nezxenka.netvision.actor.manager.PlayerDataManager;
import club.nezxenka.netvision.actor.model.NetVisionPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class DamagePreprocessor {
  private final PlayerDataManager manager;

  public DamagePreprocessor(PlayerDataManager manager) {
    this.manager = manager;
  }

  public NetVisionPlayer resolveDamager(EntityDamageByEntityEvent event) {
    if (!(event.getDamager() instanceof Player damager)) return null;
    return manager.getPlayer(damager);
  }
}
