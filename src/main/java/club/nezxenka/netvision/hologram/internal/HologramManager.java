package club.nezxenka.netvision.hologram.internal;

import club.nezxenka.netvision.NetVision;
import club.nezxenka.netvision.hologram.display.PlayerHologramRenderer;
import club.nezxenka.netvision.hologram.model.ProbabilityHistory;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

public class HologramManager {
  private final NetVision plugin;
  private final Map<UUID, Map<UUID, PlayerHologramRenderer>> viewerHolograms =
      new ConcurrentHashMap<>();
  private final Map<UUID, ProbabilityHistory> probabilityHistories = new ConcurrentHashMap<>();
  private BukkitTask updateTask;

  public HologramManager(NetVision plugin) {
    this.plugin = plugin;
    startUpdateTask();
  }

  public void addProbability(UUID playerUuid, double probability) {
    probabilityHistories
        .computeIfAbsent(playerUuid, k -> new ProbabilityHistory())
        .add(probability);
  }

  public ProbabilityHistory getHistory(UUID playerUuid) {
    return probabilityHistories.getOrDefault(playerUuid, new ProbabilityHistory());
  }

  public void enableHologram(Player viewer, Player target) {
    Map<UUID, PlayerHologramRenderer> holograms =
        viewerHolograms.computeIfAbsent(viewer.getUniqueId(), k -> new ConcurrentHashMap<>());
    if (!holograms.containsKey(target.getUniqueId())) {
      PlayerHologramRenderer hologram = new PlayerHologramRenderer(this, viewer, target);
      holograms.put(target.getUniqueId(), hologram);
      hologram.spawn();
    }
  }

  public void enableForAll(Player viewer) {
    for (Player target : Bukkit.getOnlinePlayers()) {
      if (!target.equals(viewer) && !target.hasPermission("netvision.exempt"))
        enableHologram(viewer, target);
    }
  }

  public void disableHologram(Player viewer, Player target) {
    Map<UUID, PlayerHologramRenderer> holograms = viewerHolograms.get(viewer.getUniqueId());
    if (holograms != null) {
      PlayerHologramRenderer hologram = holograms.remove(target.getUniqueId());
      if (hologram != null) hologram.remove();
      if (holograms.isEmpty()) viewerHolograms.remove(viewer.getUniqueId());
    }
  }

  public void disableForAll(Player viewer) {
    Map<UUID, PlayerHologramRenderer> holograms = viewerHolograms.remove(viewer.getUniqueId());
    if (holograms != null) {
      for (PlayerHologramRenderer hologram : holograms.values()) hologram.remove();
    }
  }

  public boolean isEnabled(Player viewer, Player target) {
    Map<UUID, PlayerHologramRenderer> holograms = viewerHolograms.get(viewer.getUniqueId());
    return holograms != null && holograms.containsKey(target.getUniqueId());
  }

  public void handlePlayerQuit(Player player) {
    disableForAll(player);
    probabilityHistories.remove(player.getUniqueId());
    for (Map<UUID, PlayerHologramRenderer> holograms : viewerHolograms.values()) {
      PlayerHologramRenderer hologram = holograms.remove(player.getUniqueId());
      if (hologram != null) hologram.remove();
    }
  }

  private void startUpdateTask() {
    updateTask =
        Bukkit.getScheduler()
            .runTaskTimer(
                plugin,
                () -> {
                  for (Map<UUID, PlayerHologramRenderer> holograms : viewerHolograms.values()) {
                    for (PlayerHologramRenderer hologram : holograms.values()) hologram.update();
                  }
                },
                1L,
                1L);
  }

  public void shutdown() {
    if (updateTask != null) updateTask.cancel();
    for (Map<UUID, PlayerHologramRenderer> holograms : viewerHolograms.values()) {
      for (PlayerHologramRenderer hologram : holograms.values()) hologram.remove();
    }
    viewerHolograms.clear();
    probabilityHistories.clear();
  }
}
