package club.nezxenka.netvision.engine.network.neural.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BufferState {
  private double buffer;
  private double lastProbability;
  private int prob90Count;
}
