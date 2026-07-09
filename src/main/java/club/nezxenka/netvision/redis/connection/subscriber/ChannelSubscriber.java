package club.nezxenka.netvision.redis.connection.subscriber;

import io.lettuce.core.pubsub.RedisPubSubAdapter;
import io.lettuce.core.pubsub.StatefulRedisPubSubConnection;
import java.util.function.Consumer;

public class ChannelSubscriber {
  public void subscribe(
      StatefulRedisPubSubConnection<String, String> conn,
      String channel,
      Consumer<String> handler) {
    conn.addListener(
        new RedisPubSubAdapter<>() {
          @Override
          public void message(String ch, String msg) {
            if (channel.equals(ch)) handler.accept(msg);
          }
        });
    conn.sync().subscribe(channel);
  }
}
