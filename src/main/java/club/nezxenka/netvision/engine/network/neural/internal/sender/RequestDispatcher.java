package club.nezxenka.netvision.engine.network.neural.sender;

import club.nezxenka.netvision.remote.connection.AIServer;
import java.util.concurrent.CompletableFuture;

public class RequestDispatcher {
  private final AIServer server;

  public RequestDispatcher(AIServer server) {
    this.server = server;
  }

  public CompletableFuture<String> dispatch(byte[] payload) {
    if (server == null)
      return CompletableFuture.failedFuture(new IllegalStateException("No AI server"));
    return server.sendRequest(payload);
  }
}
