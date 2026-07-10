package club.nezxenka.netvision.service.command.api;

import club.nezxenka.netvision.audience.api.Sender;
import org.incendo.cloud.CommandManager;

public interface NetVisionCommand {
  void register(CommandManager<Sender> manager, String rootName);
}
