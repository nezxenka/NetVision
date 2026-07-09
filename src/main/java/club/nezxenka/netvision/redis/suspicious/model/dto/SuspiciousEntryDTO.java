package club.nezxenka.netvision.redis.suspicious.model.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SuspiciousEntryDTO {
  private String playerName;
  private String serverName;
  private double buffer;
  private int ping;
  private long lastUpdate;
}
