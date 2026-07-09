package club.nezxenka.netvision.util.message;

import lombok.Getter;

@Getter
public enum Message {
  PREFIX("prefix"),
  ALERTS_ENABLED("alerts-enabled"),
  ALERTS_DISABLED("alerts-disabled"),
  ALERTS_FORMAT("alerts-format"),
  PLAYER_NOT_FOUND("player-not-found"),
  RUN_AS_PLAYER("run-as-player"),
  RELOAD_START("reload-start"),
  RELOAD_SUCCESS("reload-success"),
  BRAND_ALERTS_ENABLED("brand.alerts-enabled"),
  BRAND_ALERTS_DISABLED("brand.alerts-disabled"),
  BRAND_NOTIFICATION("brand.notification"),
  BRAND_DISCONNECT_FORGE("brand.disconnect-forge"),
  CROSS_SERVER_ALERT_PREFIX("cross-server.alert-prefix"),
  FP_SUCCESS("falsepositive.success"),
  FP_FAIL("falsepositive.fail"),
  FP_NO_DATA("falsepositive.no-data"),
  PROB_ENABLED("prob.enabled"),
  PROB_DISABLED("prob.disabled"),
  PROB_NO_DATA("prob.no-data"),
  PROB_NO_AICHECK("prob.no-aicheck"),
  PROB_FORMAT_LABEL_PROB("prob.format.label-prob"),
  PROB_FORMAT_LABEL_BUFFER("prob.format.label-buffer"),
  PROB_FORMAT_LABEL_PING("prob.format.label-ping"),
  PROB_FORMAT_SEPARATOR("prob.format.separator"),
  PROB_FORMAT_SUFFIX_PING("prob.format.suffix-ping"),
  PROFILE_NO_DATA("profile.no-data"),
  PROFILE_LINES("profile.lines"),
  HISTORY_DISABLED("history.disabled"),
  HISTORY_HEADER("history.header"),
  HISTORY_ENTRY("history.entry"),
  HISTORY_NO_VIOLATIONS("history.no-violations"),
  LOGS_HEADER("logs.header"),
  LOGS_ENTRY("logs.entry"),
  LOGS_NO_VIOLATIONS("logs.no-violations"),
  LOGS_INVALID_TIME("logs.invalid-time"),
  PUNISH_RESET_SUCCESS("punish.reset-success"),
  SUSPICIOUS_ALERTS_ENABLED("suspicious.alerts-enabled"),
  SUSPICIOUS_ALERTS_DISABLED("suspicious.alerts-disabled"),
  SUSPICIOUS_ALERT_TRIGGERED("suspicious.alert-triggered"),
  SUSPICIOUS_LIST_EMPTY("suspicious.list-empty"),
  SUSPICIOUS_LIST_HEADER("suspicious.list-header"),
  SUSPICIOUS_LIST_ENTRY("suspicious.list-entry"),
  SUSPICIOUS_TOP_NONE("suspicious.top-none"),
  SUSPICIOUS_TOP_PLAYER("suspicious.top-player"),
  STATS_LINES("stats.lines"),
  HELP_MESSAGE("help"),
  INTERNAL_ERROR("internal.error"),
  TIME_AGO("time.ago"),
  TIME_DAYS("time.days"),
  TIME_HOURS("time.hours"),
  TIME_MINUTES("time.minutes"),
  TIME_SECONDS("time.seconds");

  private final String path;

  Message(String path) {
    this.path = path;
  }
}
