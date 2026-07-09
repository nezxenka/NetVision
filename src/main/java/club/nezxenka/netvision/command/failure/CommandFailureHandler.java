package club.nezxenka.netvision.command.failure;

import club.nezxenka.netvision.command.api.SenderRequirement;
import club.nezxenka.netvision.sender.api.Sender;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.processors.requirements.RequirementFailureHandler;

public class CommandFailureHandler implements RequirementFailureHandler<Sender, SenderRequirement> {
  @Override
  public void handleFailure(
      @NonNull CommandContext<Sender> context, @NonNull SenderRequirement requirement) {
    context.sender().sendMessage(requirement.errorMessage(context.sender()));
  }
}
