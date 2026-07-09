package club.nezxenka.netvision.check.registry;

import club.nezxenka.netvision.NetVision;
import club.nezxenka.netvision.alert.internal.AlertManager;
import club.nezxenka.netvision.check.api.Check;
import club.nezxenka.netvision.check.api.PacketCheck;
import club.nezxenka.netvision.check.api.Reloadable;
import club.nezxenka.netvision.check.api.RotationCheck;
import club.nezxenka.netvision.check.packet.ai.internal.AICheck;
import club.nezxenka.netvision.check.packet.ai.internal.ActionManager;
import club.nezxenka.netvision.check.packet.misc.internal.ClientBrand;
import club.nezxenka.netvision.check.rotation.internal.AimProcessor;
import club.nezxenka.netvision.config.core.ConfigManager;
import club.nezxenka.netvision.integration.worldguard.WorldGuardManager;
import club.nezxenka.netvision.player.model.NetVisionPlayer;
import club.nezxenka.netvision.server.provider.AIServerProvider;
import club.nezxenka.netvision.util.rotation.RotationUpdate;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CheckManager {
  private final NetVisionPlayer nvPlayer;
  private final List<RotationCheck> rotationChecks = new ArrayList<>();
  private final List<PacketCheck> packetChecks = new ArrayList<>();
  private final Map<Class<? extends Check>, Check> checks = new HashMap<>();

  public CheckManager(
      NetVisionPlayer player,
      NetVision plugin,
      ConfigManager configManager,
      AIServerProvider aiServerProvider,
      WorldGuardManager worldGuardManager,
      AlertManager alertManager) {
    this.nvPlayer = player;
    registerCheck(new AimProcessor(player));
    registerCheck(new ActionManager(player, configManager));
    registerCheck(
        new AICheck(
            player, plugin, aiServerProvider, configManager, worldGuardManager, alertManager));
    registerCheck(new ClientBrand(player, configManager, alertManager));
  }

  private void registerCheck(Check check) {
    checks.put(check.getClass(), check);
    if (check instanceof RotationCheck rotationCheck) rotationChecks.add(rotationCheck);
    if (check instanceof PacketCheck packetCheck) packetChecks.add(packetCheck);
  }

  public void reloadChecks() {
    for (Check check : checks.values())
      if (check instanceof Reloadable reloadable) reloadable.reload();
  }

  public void onRotationUpdate(RotationUpdate update) {
    if (nvPlayer.isBedrockExempt()) return;
    for (RotationCheck check : rotationChecks) check.process(update);
  }

  public void onPacketReceive(PacketReceiveEvent event) {
    if (nvPlayer.isBedrockExempt()) return;
    for (PacketCheck check : packetChecks) check.onPacketReceive(event);
  }

  @SuppressWarnings("unchecked")
  public <T extends Check> T getCheck(Class<T> clazz) {
    return (T) checks.get(clazz);
  }

  public Collection<Check> getAllChecks() {
    return checks.values();
  }
}
