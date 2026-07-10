package club.nezxenka.netvision.engine.network.neural;

import club.nezxenka.netvision.NetVision;
import club.nezxenka.netvision.actor.model.NetVisionPlayer;
import club.nezxenka.netvision.core.config.ConfigManager;
import club.nezxenka.netvision.core.diagnostic.model.DebugCategory;
import club.nezxenka.netvision.engine.api.PacketModule;
import club.nezxenka.netvision.engine.api.Reloadable;
import club.nezxenka.netvision.engine.base.BaseModule;
import club.nezxenka.netvision.engine.model.ModuleInfo;
import club.nezxenka.netvision.engine.model.TickSample;
import club.nezxenka.netvision.integration.worldguard.WorldGuardManager;
import club.nezxenka.netvision.remote.connection.AIServer;
import club.nezxenka.netvision.remote.model.AIResponse;
import club.nezxenka.netvision.remote.provider.AIServerProvider;
import club.nezxenka.netvision.serialize.model.TickDataFB;
import club.nezxenka.netvision.serialize.model.TickDataSequenceFB;
import club.nezxenka.netvision.service.signal.internal.SignalManager;
import club.nezxenka.netvision.service.signal.model.SignalType;
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

@ModuleInfo(name = "AICheck_Internal")
public class NeuralAnalyzer extends BaseModule implements PacketModule, Reloadable {

  private final NetVision plugin;
  private final AIServerProvider aiServerProvider;
  private final ConfigManager configManager;
  private final WorldGuardManager worldGuardManager;
  private final SignalManager alertManager;
  private static final int MAX_TICK_HISTORY = 200;
  private int step;
  private AIServer aiServer;
  private Deque<TickSample> ticks;
  private final Deque<TickSample> tickHistory =
      new ArrayDeque<>() {
        @Override
        public boolean add(TickSample e) {
          if (this.size() >= MAX_TICK_HISTORY) this.removeFirst();
          return super.add(e);
        }
      };
  private int ticksStep;

  @Getter private double buffer = 0.0;

  @Getter private double lastProbability = 0.0;

  @Getter private int prob90 = 0;

  private boolean aiDamageReductionEnabled;

  public List<TickSample> getTickHistory() {
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

  public NeuralAnalyzer(
      NetVisionPlayer nvPlayer,
      NetVision plugin,
      AIServerProvider aiServerProvider,
      ConfigManager configManager,
      WorldGuardManager worldGuardManager,
      SignalManager alertManager) {
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
              "[NeuralAnalyzer] AI server is not available for player "
                  + nvPlayer.getPlayer().getName());
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
    TickSample tickData = new TickSample(nvPlayer);
    ticks.add(tickData);
    tickHistory.add(tickData);
    if (++ticksStep < step) return;
    ticksStep = 0;
    sendData();
  }

  private void sendData() {
    List<TickSample> snapshot = new ArrayList<>(ticks);
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
                        SignalType.SUSPICIOUS));
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
              "[NeuralAnalyzer] Failed to parse AI response for "
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

  private byte[] serialize(List<TickSample> data) {
    FlatBufferBuilder builder = BUILDER.get();
    try {
      builder.clear();
      int[] tickOffsets = new int[data.size()];
      for (int i = 0; i < data.size(); i++) {
        TickSample td = data.get(i);
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
      plugin
          .getLogger()
          .warning("[NeuralAnalyzer] Failed to serialize tick data: " + e.getMessage());
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
      plugin
          .getLogger()
          .warning("[NeuralAnalyzer] Failed to save collected data: " + e.getMessage());
    }
  }
}
