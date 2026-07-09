package club.nezxenka.netvision.config.core;

import club.nezxenka.netvision.NetVision;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

@Getter
public class ConfigManager {

  private final NetVision plugin;
  private FileConfiguration config;
  private FileConfiguration punishments;
  private boolean aiEnabled;
  private String aiServerUrl;
  private String aiApiKey;

  @Setter private int aiSequence;

  private int aiStep;
  private double aiFlag;
  private double aiResetOnFlag;
  private double aiBufferMultiplier;
  private double aiBufferDecrease;
  private boolean aiDamageReductionEnabled;
  private double aiDamageReductionProb;
  private double aiDamageReductionMultiplier;
  private boolean aiWorldGuardEnabled;
  private List<String> aiDisabledRegions;
  private boolean aiCollectModeEnabled;
  private String aiCollectModeLabel;
  private List<Pattern> ignoredClientPatterns;
  private boolean disconnectBlacklistedForge;
  private double suspiciousAlertsBuffer;
  private List<String> enabledDebugCategories;
  private boolean bedrockExemptEnabled;

  public ConfigManager(NetVision plugin) {
    this.plugin = plugin;
    loadConfigs();
  }

  public void reloadConfig() {
    loadConfigs();
  }

  private void loadConfigs() {
    plugin.saveDefaultConfig();
    plugin.reloadConfig();
    this.config = plugin.getConfig();
    File punishmentsFile = new File(plugin.getDataFolder(), "punishments.yml");
    if (!punishmentsFile.exists()) {
      plugin.saveResource("punishments.yml", false);
    }
    this.punishments = YamlConfiguration.loadConfiguration(punishmentsFile);
    loadValues();
  }

  private void loadValues() {
    aiEnabled = config.getBoolean("ai.enabled", false);
    aiServerUrl = config.getString("ai.server", "");
    aiApiKey = config.getString("ai.api-key", "API-KEY");
    aiSequence = config.getInt("ai.sequence", 40);
    aiStep = config.getInt("ai.step", 10);
    aiFlag = config.getDouble("ai.buffer.flag", 50.0);
    aiResetOnFlag = config.getDouble("ai.buffer.reset-on-flag", 25.0);
    aiBufferMultiplier = config.getDouble("ai.buffer.multiplier", 100.0);
    aiBufferDecrease = config.getDouble("ai.buffer.decrease", 0.25);
    aiDamageReductionEnabled = config.getBoolean("ai.damage-reduction.enabled", true);
    aiDamageReductionProb = config.getDouble("ai.damage-reduction.prob", 0.9);
    aiDamageReductionMultiplier = config.getDouble("ai.damage-reduction.multiplier", 1.0);
    aiWorldGuardEnabled = config.getBoolean("ai.worldguard.enabled", true);
    aiDisabledRegions =
        config.getStringList("ai.worldguard.disabled-regions").stream()
            .map(String::toLowerCase)
            .collect(Collectors.toList());
    aiCollectModeEnabled = config.getBoolean("ai.collect-mode.enabled", false);
    aiCollectModeLabel = config.getString("ai.collect-mode.label", "legit");
    ignoredClientPatterns = new ArrayList<>();
    for (String pattern : config.getStringList("client-brand.ignored-clients")) {
      try {
        ignoredClientPatterns.add(Pattern.compile(pattern));
      } catch (PatternSyntaxException e) {
        plugin.getLogger().warning("[ClientBrand] Invalid regex pattern in config: " + pattern);
      }
    }
    disconnectBlacklistedForge =
        config.getBoolean("client-brand.disconnect-blacklisted-forge-versions", true);
    suspiciousAlertsBuffer = config.getDouble("suspicious.alerts.buffer", 25.0);
    enabledDebugCategories = config.getStringList("debug.enabled-categories");
    if (enabledDebugCategories == null) {
      enabledDebugCategories = Collections.emptyList();
    }
    bedrockExemptEnabled = config.getBoolean("exemptions.bedrock", true);
  }

  public boolean isBedrockExemptEnabled() {
    return bedrockExemptEnabled;
  }

  public boolean isClientIgnored(String brand) {
    for (Pattern pattern : ignoredClientPatterns) {
      if (pattern.matcher(brand).find()) {
        return true;
      }
    }
    return false;
  }
}
