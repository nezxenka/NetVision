package club.nezxenka.netvision.service.signal.model.config;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SignalConfiguration {
  private boolean logToConsole;
  private String alertFormat;
  private String brandAlertFormat;
}
