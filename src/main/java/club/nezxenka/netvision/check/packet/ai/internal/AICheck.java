package club.nezxenka.netvision.check.packet.ai.internal;

import club.nezxenka.netvision.NetVision;
import club.nezxenka.netvision.alert.internal.AlertManager;
import club.nezxenka.netvision.alert.model.AlertType;
import club.nezxenka.netvision.check.api.PacketCheck;
import club.nezxenka.netvision.check.api.Reloadable;
import club.nezxenka.netvision.check.metadata.CheckData;
import club.nezxenka.netvision.check.pipeline.AbstractCheck;
import club.nezxenka.netvision.config.core.ConfigManager;
import club.nezxenka.netvision.data.TickData;
import club.nezxenka.netvision.debug.model.DebugCategory;
import club.nezxenka.netvision.flatbuffer.model.TickDataFB;
import club.nezxenka.netvision.flatbuffer.model.TickDataSequenceFB;
import club.nezxenka.netvision.integration.worldguard.WorldGuardManager;
import club.nezxenka.netvision.player.model.NetVisionPlayer;
import club.nezxenka.netvision.server.connection.AIServer;
import club.nezxenka.netvision.server.model.AIResponse;
import club.nezxenka.netvision.server.provider.AIServerProvider;
import club.nezxenka.netvision.util.message.Message;
import club.nezxenka.netvision.util.message.MessageUtil;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.google.flatbuffers.FlatBufferBuilder;
import com.google.gson.Gson;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import lombok.Getter;

@CheckData(name = "AICheck_Internal")
public class AICheck extends AbstractCheck implements PacketCheck, Reloadable {

  private final NetVision plugin;
  private final AIServerProvider aiServerProvider;
  private final ConfigManager configManager;
  private final WorldGuardManager worldGuardManager;
  private final AlertManager alertManager;
  private static final int MAX_TICK_HISTORY = 200;
  private int step;
  private AIServer aiServer;
  private Deque<TickData> ticks;
  private final Deque<TickData> tickHistory =
      new ArrayDeque<>() {
        @Override
        public boolean add(TickData e) {
          if (this.size() >= MAX_TICK_HISTORY) this.removeFirst();
          return super.add(e);
        }
      };
  private int ticksStep;

  @Getter private double buffer = 0.0;

  @Getter private double lastProbability = 0.0;

  @Getter private int prob90 = 0;

  private boolean aiDamageReductionEnabled;

  public List<TickData> getTickHistory() {
    return new ArrayList<>(tickHistory);
  }

  private double aiDamageReductionProb;
  private double aiDamageReductionMultiplier;
  private double flag;
  private double bufferResetOnFlag;
  private double bufferMultiplier;
  private double bufferDecrease;
  private double suspiciousAlertBuffer;
  private static final double CHEAT_PROBABILITY = 0.99;
  private static final Gson GSON = new Gson();
  private static final ThreadLocal<FlatBufferBuilder> BUILDER =
      ThreadLocal.withInitial(
          () -> {
            FlatBufferBuilder b = new FlatBufferBuilder(1024);
            return b;
          });

  public AICheck(
      NetVisionPlayer nvPlayer,
      NetVision plugin,
      AIServerProvider aiServerProvider,
      ConfigManager configManager,
      WorldGuardManager worldGuardManager,
      AlertManager alertManager) {
    super(nvPlayer);
    this.plugin = plugin;
    this.aiServerProvider = aiServerProvider;
    this.configManager = configManager;
    this.worldGuardManager = worldGuardManager;
    this.alertManager = alertManager;
    reload();
  }

  @Override
  public void reload() {
    ticks = new ArrayDeque<>();
    ticksStep = 0;
    step = configManager.getAiStep();
    flag = configManager.getAiFlag();
    bufferResetOnFlag = configManager.getAiResetOnFlag();
    bufferMultiplier = configManager.getAiBufferMultiplier();
    bufferDecrease = configManager.getAiBufferDecrease();
    aiDamageReductionEnabled = configManager.isAiDamageReductionEnabled();
    aiDamageReductionProb = configManager.getAiDamageReductionProb();
    aiDamageReductionMultiplier = configManager.getAiDamageReductionMultiplier();
    suspiciousAlertBuffer = configManager.getSuspiciousAlertsBuffer();
    aiServer = aiServerProvider.get();
    if (aiServer == null)
      plugin
          .getLogger()
          .warning(
              "[AICheck] AI server is not available for player " + nvPlayer.getPlayer().getName());
  }

  @Override
  public void onPacketReceive(PacketReceiveEvent event) {
    if (event.getPacketType() != PacketType.Play.Client.PLAYER_POSITION
        && event.getPacketType() != PacketType.Play.Client.PLAYER_POSITION_AND_ROTATION
        && event.getPacketType() != PacketType.Play.Client.PLAYER_ROTATION) return;
    if (aiServer == null) return;
    if (nvPlayer.ticksSinceAttack < configManager.getAiSequence()) return;
    if (aiDamageReductionEnabled
        && configManager.isAiWorldGuardEnabled()
        && worldGuardManager != null
        && worldGuardManager.isPlayerInDisabledRegion(nvPlayer.getPlayer())) return;
    TickData tickData = new TickData(nvPlayer);
    ticks.add(tickData);
    tickHistory.add(tickData);
    if (++ticksStep < step) return;
    ticksStep = 0;
    sendData();
  }

  private void sendData() {
    List<TickData> snapshot = new ArrayList<>(ticks);
    ticks.clear();
    if (snapshot.isEmpty()) return;
    byte[] serialized = serialize(snapshot);
    if (serialized == null) return;
    if (configManager.isAiCollectModeEnabled()) {
      saveToFile(serialized);
      return;
    }
    CompletableFuture<String> future = aiServer.sendRequest(serialized);
    future.thenAccept(this::onResponse).exceptionally(this::onError);
  }

  private void onResponse(String json) {
    try {
      AIResponse response = GSON.fromJson(json, AIResponse.class);
      double probability = response.probability();
      this.lastProbability = probability;
      plugin.getHologramManager().addProbability(nvPlayer.getUuid(), probability);
      plugin
          .getChickenCoopMenu()
          .addOrUpdatePlayer(nvPlayer.getUuid(), nvPlayer.getPlayer().getName(), probability);
      if (probability > 0.9) prob90++;
      else if (probability < 0.1) prob90 = Math.max(0, prob90 - 1);
      if (probability > CHEAT_PROBABILITY)
        buffer = Math.min(buffer + probability * bufferMultiplier, flag);
      else buffer = Math.max(0, buffer - bufferDecrease);
      if (buffer >= flag) {
        flag(getDebugInfo(probability));
        buffer = Math.max(0, buffer - bufferResetOnFlag);
      }
      if (probability > 0.9 && aiDamageReductionEnabled) {
        if (Math.random() < aiDamageReductionProb)
          nvPlayer.setDmgMultiplier(aiDamageReductionMultiplier);
        else nvPlayer.setDmgMultiplier(1.0);
      } else {
        nvPlayer.setDmgMultiplier(1.0);
      }
      if (buffer >= suspiciousAlertBuffer && probability > 0.9) {
        plugin
            .getServer()
            .getScheduler()
            .runTask(
                plugin,
                () ->
                    alertManager.send(
                        MessageUtil.getMessage(
                            Message.SUSPICIOUS_ALERT_TRIGGERED,
                            "player",
                            nvPlayer.getPlayer().getName(),
                            "buffer",
                            String.format("%.1f", buffer)),
                        AlertType.SUSPICIOUS));
      }
      plugin
          .getDebugManager()
          .log(
              DebugCategory.AI_PROBABILITY,
              nvPlayer.getPlayer().getName()
                  + " prob="
                  + String.format("%.4f", probability)
                  + " buffer="
                  + String.format("%.2f", buffer));
    } catch (Exception e) {
      plugin
          .getLogger()
          .warning(
              "[AICheck] Failed to parse AI response for "
                  + nvPlayer.getPlayer().getName()
                  + ": "
                  + e.getMessage());
    }
  }

  private Void onError(Throwable throwable) {
    plugin
        .getDebugManager()
        .log(
            DebugCategory.AI_TIMEOUT,
            nvPlayer.getPlayer().getName() + " timeout/error: " + throwable.getMessage());
    return null;
  }

  private String getDebugInfo(double probability) {
    return String.format("prob=%.4f vl=%.1f", probability, buffer);
  }

  private byte[] serialize(List<TickData> data) {
    FlatBufferBuilder builder = BUILDER.get();
    try {
      builder.clear();
      int[] tickOffsets = new int[data.size()];
      for (int i = 0; i < data.size(); i++) {
        TickData td = data.get(i);
        tickOffsets[i] =
            TickDataFB.createTickData(
                builder,
                td.deltaYaw,
                td.deltaPitch,
                td.accelYaw,
                td.accelPitch,
                td.jerkPitch,
                td.jerkYaw,
                td.gcdErrorYaw,
                td.gcdErrorPitch);
      }
      int ticksVector = TickDataSequenceFB.createTicksVector(builder, tickOffsets);
      int root = TickDataSequenceFB.createTickDataSequence(builder, ticksVector);
      TickDataSequenceFB.finishTickDataSequenceBuffer(builder, root);
      return builder.sizedByteArray();
    } catch (Exception e) {
      plugin.getLogger().warning("[AICheck] Failed to serialize tick data: " + e.getMessage());
      return null;
    }
  }

  private void saveToFile(byte[] data) {
    String label = configManager.getAiCollectModeLabel();
    File dir = new File(plugin.getDataFolder(), "collected" + File.separator + label);
    if (!dir.exists()) dir.mkdirs();
    File file = new File(dir, System.currentTimeMillis() + ".bin");
    try {
      Files.write(file.toPath(), data);
    } catch (IOException e) {
      plugin.getLogger().warning("[AICheck] Failed to save collected data: " + e.getMessage());
    }
  }
}
