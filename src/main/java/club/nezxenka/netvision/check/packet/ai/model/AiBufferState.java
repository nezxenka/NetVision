package club.nezxenka.netvision.check.packet.ai.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AiBufferState {
  private double buffer;
  private double lastProbability;
  private int prob90Count;
}
