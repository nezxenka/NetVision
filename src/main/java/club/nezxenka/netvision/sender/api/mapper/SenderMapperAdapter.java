package club.nezxenka.netvision.sender.api.mapper;

import club.nezxenka.netvision.NetVision;
import club.nezxenka.netvision.sender.api.Sender;
import club.nezxenka.netvision.sender.factory.SenderFactory;
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
