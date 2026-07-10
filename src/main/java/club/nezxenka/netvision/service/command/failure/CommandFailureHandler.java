package club.nezxenka.netvision.service.command.failure;

import club.nezxenka.netvision.audience.api.Sender;
import club.nezxenka.netvision.service.command.api.SenderRequirement;
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
