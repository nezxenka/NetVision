package club.nezxenka.netvision.alert.model.config;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AlertConfiguration {
  private boolean logToConsole;
  private String alertFormat;
  private String brandAlertFormat;
}
