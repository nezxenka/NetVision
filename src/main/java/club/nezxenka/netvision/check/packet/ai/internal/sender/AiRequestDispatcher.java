package club.nezxenka.netvision.check.packet.ai.internal.sender;

import club.nezxenka.netvision.server.connection.AIServer;
import java.util.concurrent.CompletableFuture;

public class AiRequestDispatcher {
  private final AIServer server;

  public AiRequestDispatcher(AIServer server) {
    this.server = server;
  }

  public CompletableFuture<String> dispatch(byte[] payload) {
    if (server == null)
      return CompletableFuture.failedFuture(new IllegalStateException("No AI server"));
    return server.sendRequest(payload);
  }
}
