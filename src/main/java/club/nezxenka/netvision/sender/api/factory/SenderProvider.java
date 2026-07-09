package club.nezxenka.netvision.sender.api.factory;

import club.nezxenka.netvision.sender.api.Sender;

public interface SenderProvider {
  Sender create(org.bukkit.command.CommandSender base);
}
