package club.nezxenka.netvision.command.api.executor;

import club.nezxenka.netvision.sender.api.Sender;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CommandExecutorContext {
  private Sender sender;
  private String rootName;
  private String subCommand;
}
