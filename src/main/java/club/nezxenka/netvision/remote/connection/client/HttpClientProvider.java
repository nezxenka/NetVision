package club.nezxenka.netvision.remote.connection.client;

import java.net.http.HttpClient;
import java.time.Duration;

public class HttpClientProvider {
  private static final HttpClient INSTANCE =
      HttpClient.newBuilder()
          .version(HttpClient.Version.HTTP_2)
          .connectTimeout(Duration.ofSeconds(10))
          .build();

  public HttpClient get() {
    return INSTANCE;
  }
}
