package club.nezxenka.netvision.util.latency.async;

import com.github.retrooper.packetevents.netty.channel.ChannelHelper;
import io.netty.channel.Channel;

public class AsyncTaskRunner {
  public void runInEventLoop(Channel channel, Runnable task) {
    ChannelHelper.runInEventLoop(channel, task);
  }

  public void runDirect(Runnable task) {
    task.run();
  }
}
