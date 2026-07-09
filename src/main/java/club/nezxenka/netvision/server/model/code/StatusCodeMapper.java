package club.nezxenka.netvision.server.model.code;

import club.nezxenka.netvision.server.connection.AIServer;

public class StatusCodeMapper {
  public AIServer.ResponseCode map(int httpCode) {
    return AIServer.ResponseCode.fromStatusCode(httpCode);
  }

  public boolean isServerError(int code) {
    return code >= 500;
  }

  public boolean isClientError(int code) {
    return code >= 400 && code < 500;
  }
}
