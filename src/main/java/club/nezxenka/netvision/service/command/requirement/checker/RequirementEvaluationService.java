package club.nezxenka.netvision.service.command.requirement.checker;

import club.nezxenka.netvision.audience.api.Sender;
import club.nezxenka.netvision.service.command.api.SenderRequirement;
import org.incendo.cloud.context.CommandContext;

public class RequirementEvaluationService {
  public boolean evaluate(SenderRequirement requirement, CommandContext<Sender> context) {
    return requirement.evaluateRequirement(context);
  }

  public boolean isPlayer(CommandContext<Sender> context) {
    return context.sender().isPlayer();
  }
}
