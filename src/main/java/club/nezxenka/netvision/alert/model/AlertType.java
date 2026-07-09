package club.nezxenka.netvision.alert.model;

import club.nezxenka.netvision.util.message.Message;
import lombok.Getter;

@Getter
public enum AlertType {
  REGULAR("netvision.alerts", Message.ALERTS_ENABLED, Message.ALERTS_DISABLED),
  BRAND("netvision.brand", Message.BRAND_ALERTS_ENABLED, Message.BRAND_ALERTS_DISABLED),
  SUSPICIOUS(
      "netvision.suspicious.alerts",
      Message.SUSPICIOUS_ALERTS_ENABLED,
      Message.SUSPICIOUS_ALERTS_DISABLED);

  private final String permission;
  private final Message enabledMessage;
  private final Message disabledMessage;

  AlertType(String permission, Message enabledMessage, Message disabledMessage) {
    this.permission = permission;
    this.enabledMessage = enabledMessage;
    this.disabledMessage = disabledMessage;
  }
}
