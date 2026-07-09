package club.nezxenka.netvision.database.model;

import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PlayerMenuData {
  private final UUID uuid;
  private final String playerName;
  private final List<Double> probabilities;
}
