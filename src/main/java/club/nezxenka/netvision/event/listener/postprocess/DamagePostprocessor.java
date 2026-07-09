package club.nezxenka.netvision.event.listener.postprocess;

import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class DamagePostprocessor {
  public void applyReduction(EntityDamageByEntityEvent event, double multiplier) {
    if (multiplier < 1.0) event.setDamage(event.getDamage() * multiplier);
  }
}
