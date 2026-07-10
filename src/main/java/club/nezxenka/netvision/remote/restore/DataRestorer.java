package club.nezxenka.netvision.remote.restore;

import club.nezxenka.netvision.engine.model.TickSample;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.logging.Level;
import org.bukkit.plugin.java.JavaPlugin;

public class DataRestorer {

  private final JavaPlugin plugin;
  private final File restoredDataFolder;

  public DataRestorer(JavaPlugin plugin) {
    this.plugin = plugin;
    this.restoredDataFolder = new File(plugin.getDataFolder(), "restored_data");
    if (!restoredDataFolder.exists()) restoredDataFolder.mkdirs();
  }

  public boolean restoreData(String playerName, List<TickSample> history) {
    if (history == null || history.isEmpty()) return false;
    String timestamp =
        LocalDateTime.now(ZoneId.systemDefault())
            .format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
    String fileName = playerName + "_" + timestamp + ".csv";
    File file = new File(restoredDataFolder, fileName);
    try (PrintWriter writer =
        new PrintWriter(Files.newBufferedWriter(file.toPath(), StandardCharsets.UTF_8))) {
      writer.println(
          "is_cheating,delta_yaw,delta_pitch,accel_yaw,accel_pitch,jerk_yaw,jerk_pitch,gcd_error_yaw,gcd_error_pitch");
      for (TickSample tick : history)
        writer.println(
            "0,"
                + String.format("%.6f", tick.deltaYaw)
                + ","
                + String.format("%.6f", tick.deltaPitch)
                + ","
                + String.format("%.6f", tick.accelYaw)
                + ","
                + String.format("%.6f", tick.accelPitch)
                + ","
                + String.format("%.6f", tick.jerkYaw)
                + ","
                + String.format("%.6f", tick.jerkPitch)
                + ","
                + String.format("%.6f", tick.gcdErrorYaw)
                + ","
                + String.format("%.6f", tick.gcdErrorPitch));
      return true;
    } catch (IOException e) {
      plugin.getLogger().log(Level.SEVERE, "Failed to restore data for " + playerName, e);
      return false;
    }
  }

  public File getRestoredDataFolder() {
    return restoredDataFolder;
  }
}
