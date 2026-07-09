package club.nezxenka.netvision.redis.suspicious.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class SuspiciousSnapshot {
  private final String server;
  private final String uuid;
  private final String name;
  private final double buffer;
  private final int ping;
  private final long updatedAt;

  @JsonCreator
  public SuspiciousSnapshot(
      @JsonProperty("server") String server,
      @JsonProperty("uuid") String uuid,
      @JsonProperty("name") String name,
      @JsonProperty("buffer") double buffer,
      @JsonProperty("ping") int ping,
      @JsonProperty("updatedAt") long updatedAt) {
    this.server = server;
    this.uuid = uuid;
    this.name = name;
    this.buffer = buffer;
    this.ping = ping;
    this.updatedAt = updatedAt;
  }

  @JsonProperty("server")
  public String getServer() {
    return server;
  }

  @JsonProperty("uuid")
  public String getUuid() {
    return uuid;
  }

  @JsonProperty("name")
  public String getName() {
    return name;
  }

  @JsonProperty("buffer")
  public double getBuffer() {
    return buffer;
  }

  @JsonProperty("ping")
  public int getPing() {
    return ping;
  }

  @JsonProperty("updatedAt")
  public long getUpdatedAt() {
    return updatedAt;
  }
}
