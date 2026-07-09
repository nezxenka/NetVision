package club.nezxenka.netvision.command.api;

import club.nezxenka.netvision.sender.api.Sender;
import org.incendo.cloud.CommandManager;

public interface NetVisionCommand {
  void register(CommandManager<Sender> manager, String rootName);
}
