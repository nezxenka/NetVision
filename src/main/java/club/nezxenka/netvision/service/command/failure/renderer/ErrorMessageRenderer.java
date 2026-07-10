package club.nezxenka.netvision.service.command.failure.renderer;

import club.nezxenka.netvision.util.message.Message;
import club.nezxenka.netvision.util.message.MessageUtil;
import net.kyori.adventure.text.Component;

public class ErrorMessageRenderer {
  public Component playerOnly() {
    return MessageUtil.getMessage(Message.RUN_AS_PLAYER);
  }

  public Component playerNotFound() {
    return MessageUtil.getMessage(Message.PLAYER_NOT_FOUND);
  }

  public Component internalError() {
    return MessageUtil.getMessage(Message.INTERNAL_ERROR);
  }
}
