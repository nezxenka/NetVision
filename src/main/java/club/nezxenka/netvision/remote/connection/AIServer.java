package club.nezxenka.netvision.remote.connection;

import club.nezxenka.netvision.NetVision;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpTimeoutException;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import lombok.Getter;

public final class AIServer {
  private static final Duration CONNECT_TIMEOUT = Duration.ofSeconds(10);
  private static final Duration REQUEST_TIMEOUT = Duration.ofSeconds(5);
  private static volatile long waiting = 0;
  private static final HttpClient HTTP_CLIENT =
      HttpClient.newBuilder()
          .version(HttpClient.Version.HTTP_2)
          .connectTimeout(CONNECT_TIMEOUT)
          .build();
  private final URI serverUri;
  private final String apiKey;
  private final Executor bukkitExecutor;
  private final NetVision plugin;

  public AIServer(NetVision plugin, String url, String apiKey) {
    this.plugin = plugin;
    this.serverUri = URI.create(url);
    this.apiKey = apiKey;
    this.bukkitExecutor = runnable -> plugin.getServer().getScheduler().runTask(plugin, runnable);
  }

  public CompletableFuture<String> sendRequest(byte[] playerData) {
    if (System.currentTimeMillis() < waiting)
      return CompletableFuture.failedFuture(
          new RequestException(ResponseCode.WAITING, "Waiting 15 sec"));
    HttpRequest request =
        HttpRequest.newBuilder(serverUri)
            .header("Content-Type", "application/octet-stream")
            .header("User-Agent", "NetVision/" + plugin.getDescription().getVersion())
            .header("X-API-Key", this.apiKey)
            .header("Accept", "application/json")
            .POST(HttpRequest.BodyPublishers.ofByteArray(playerData))
            .timeout(REQUEST_TIMEOUT)
            .build();
    return HTTP_CLIENT
        .sendAsync(request, HttpResponse.BodyHandlers.ofString())
        .thenApplyAsync(this::catchResponse, bukkitExecutor)
        .exceptionallyComposeAsync(this::catchException, bukkitExecutor);
  }

  private String catchResponse(HttpResponse<String> response) {
    final int statusCode = response.statusCode();
    if (statusCode >= 500 || statusCode == 403) waiting = System.currentTimeMillis() + 15000;
    if (statusCode >= 300 || statusCode < 200)
      throw new RequestException(
          ResponseCode.fromStatusCode(statusCode),
          "HTTP Status " + statusCode + ": " + response.body());
    return response.body();
  }

  private <U> CompletableFuture<U> catchException(Throwable throwable) {
    final Throwable cause =
        (throwable instanceof java.util.concurrent.CompletionException
                && throwable.getCause() != null)
            ? throwable.getCause()
            : throwable;
    if (cause instanceof RequestException) return CompletableFuture.failedFuture(cause);
    final boolean isTimeout = cause instanceof HttpTimeoutException;
    if (!isTimeout) waiting = System.currentTimeMillis() + 15000;
    final ResponseCode code = isTimeout ? ResponseCode.TIMEOUT : ResponseCode.NETWORK_ERROR;
    return CompletableFuture.failedFuture(
        new RequestException(code, "Request failed: " + cause.getMessage(), cause));
  }

  public enum ResponseCode {
    SUCCESS(200),
    BAD_REQUEST(400),
    UNAUTHORIZED(403),
    INVALID_SEQUENCE(422),
    SERVER_ERROR(500),
    TIMEOUT(-1),
    NETWORK_ERROR(-2),
    PARSE_ERROR(-3),
    WAITING(-5),
    UNKNOWN_ERROR(-4);
    private final int httpCode;

    ResponseCode(int httpCode) {
      this.httpCode = httpCode;
    }

    public static ResponseCode fromStatusCode(int code) {
      for (ResponseCode value : values()) if (value.httpCode == code) return value;
      return code >= 500 ? SERVER_ERROR : (code >= 400 ? BAD_REQUEST : UNKNOWN_ERROR);
    }
  }

  public static final class RequestException extends RuntimeException {
    @Getter private final ResponseCode code;

    public RequestException(ResponseCode code, String message) {
      super(message);
      this.code = code;
    }

    public RequestException(ResponseCode code, String message, Throwable cause) {
      super(message, cause);
      this.code = code;
    }
  }
}
