package club.nezxenka.netvision.engine.coordinator;

import club.nezxenka.netvision.NetVision;
import club.nezxenka.netvision.actor.model.NetVisionPlayer;
import club.nezxenka.netvision.core.config.ConfigManager;
import club.nezxenka.netvision.engine.aim.AimEvaluator;
import club.nezxenka.netvision.engine.api.AimModule;
import club.nezxenka.netvision.engine.api.AnalysisModule;
import club.nezxenka.netvision.engine.api.PacketModule;
import club.nezxenka.netvision.engine.api.Reloadable;
import club.nezxenka.netvision.engine.network.misc.BrandScanner;
import club.nezxenka.netvision.engine.network.neural.ActionTracker;
import club.nezxenka.netvision.engine.network.neural.NeuralAnalyzer;
import club.nezxenka.netvision.integration.worldguard.WorldGuardManager;
import club.nezxenka.netvision.remote.provider.AIServerProvider;
import club.nezxenka.netvision.service.signal.internal.SignalManager;
import club.nezxenka.netvision.util.rotation.RotationUpdate;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ModuleCoordinator {
  private final NetVisionPlayer nvPlayer;
  private final List<AimModule> aimModules = new ArrayList<>();
  private final List<PacketModule> packetModules = new ArrayList<>();
  private final Map<Class<? extends AnalysisModule>, AnalysisModule> modules = new HashMap<>();

  public ModuleCoordinator(
      NetVisionPlayer player,
      NetVision plugin,
      ConfigManager configManager,
      AIServerProvider aiServerProvider,
      WorldGuardManager worldGuardManager,
      SignalManager alertManager) {
    this.nvPlayer = player;
    registerModule(new AimEvaluator(player));
    registerModule(new ActionTracker(player, configManager));
    registerModule(
        new NeuralAnalyzer(
            player, plugin, aiServerProvider, configManager, worldGuardManager, alertManager));
    registerModule(new BrandScanner(player, configManager, alertManager));
  }

  private void registerModule(AnalysisModule module) {
    modules.put(module.getClass(), module);
    if (module instanceof AimModule aimModule) aimModules.add(aimModule);
    if (module instanceof PacketModule packetModule) packetModules.add(packetModule);
  }

  public void reloadModules() {
    for (AnalysisModule module : modules.values())
      if (module instanceof Reloadable reloadable) reloadable.reload();
  }

  public void onRotationUpdate(RotationUpdate update) {
    if (nvPlayer.isBedrockExempt()) return;
    for (AimModule module : aimModules) module.process(update);
  }

  public void onPacketReceive(PacketReceiveEvent event) {
    if (nvPlayer.isBedrockExempt()) return;
    for (PacketModule module : packetModules) module.onPacketReceive(event);
  }

  @SuppressWarnings("unchecked")
  public <T extends AnalysisModule> T getModule(Class<T> clazz) {
    return (T) modules.get(clazz);
  }

  public Collection<AnalysisModule> getAllModules() {
    return modules.values();
  }
}
