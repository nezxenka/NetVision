package club.nezxenka.netvision.service.command.impl.help.renderer;

import club.nezxenka.netvision.util.message.Message;
import club.nezxenka.netvision.util.message.MessageUtil;
import java.util.List;
import net.kyori.adventure.text.Component;

public class HelpMessageComposer {
  public List<Component> compose() {
    return MessageUtil.getMessageList(Message.HELP_MESSAGE);
  }
}
