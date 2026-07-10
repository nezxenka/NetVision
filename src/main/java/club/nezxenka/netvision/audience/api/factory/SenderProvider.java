package club.nezxenka.netvision.audience.api.factory;

import club.nezxenka.netvision.audience.api.Sender;

public interface SenderProvider {
  Sender create(org.bukkit.command.CommandSender base);
}
