package club.nezxenka.netvision.service.command.requirement;

import club.nezxenka.netvision.audience.api.Sender;
import club.nezxenka.netvision.service.command.api.SenderRequirement;
import club.nezxenka.netvision.util.message.Message;
import club.nezxenka.netvision.util.message.MessageUtil;
import net.kyori.adventure.text.Component;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.incendo.cloud.context.CommandContext;

public final class PlayerSenderRequirement implements SenderRequirement {
  public static final PlayerSenderRequirement PLAYER_SENDER_REQUIREMENT =
      new PlayerSenderRequirement();

  @Override
  public @NonNull Component errorMessage(Sender sender) {
    return MessageUtil.getMessage(Message.RUN_AS_PLAYER);
  }

  @Override
  public boolean evaluateRequirement(@NonNull CommandContext<Sender> commandContext) {
    return commandContext.sender().isPlayer();
  }
}
