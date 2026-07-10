package club.nezxenka.netvision.remote.restore.writer;

import club.nezxenka.netvision.engine.model.TickSample;
import java.io.PrintWriter;
import java.util.List;

public class CsvDataWriter {
  public void write(PrintWriter writer, List<TickSample> ticks) {
    for (TickSample tick : ticks) {
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
    }
  }
}
