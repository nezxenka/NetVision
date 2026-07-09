package club.nezxenka.netvision.redis.alert.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class CrossServerAlert {
  private final String origin;
  private final String server;
  private final String type;
  private final String component;

  @JsonCreator
  public CrossServerAlert(
      @JsonProperty("origin") String origin,
      @JsonProperty("server") String server,
      @JsonProperty("type") String type,
      @JsonProperty("component") String component) {
    this.origin = origin;
    this.server = server;
    this.type = type;
    this.component = component;
  }

  @JsonProperty("origin")
  public String getOrigin() {
    return origin;
  }

  @JsonProperty("server")
  public String getServer() {
    return server;
  }

  @JsonProperty("type")
  public String getType() {
    return type;
  }

  @JsonProperty("component")
  public String getComponent() {
    return component;
  }
}
