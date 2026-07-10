package club.nezxenka.netvision.service.command.api.executor;

import club.nezxenka.netvision.audience.api.Sender;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CommandExecutorContext {
  private Sender sender;
  private String rootName;
  private String subCommand;
}
