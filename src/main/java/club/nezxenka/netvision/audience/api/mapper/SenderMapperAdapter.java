package club.nezxenka.netvision.audience.api.mapper;

import club.nezxenka.netvision.NetVision;
import club.nezxenka.netvision.audience.api.Sender;
import club.nezxenka.netvision.audience.factory.SenderFactory;
import org.bukkit.command.CommandSender;

public class SenderMapperAdapter {
  private final SenderFactory factory;

  public SenderMapperAdapter(NetVision plugin) {
    this.factory = new SenderFactory(plugin);
  }

  public Sender map(CommandSender base) {
    return factory.map(base);
  }

  public CommandSender reverse(Sender mapped) {
    return factory.reverse(mapped);
  }
}
