package club.nezxenka.netvision.command.requirement.checker;

import club.nezxenka.netvision.command.api.SenderRequirement;
import club.nezxenka.netvision.sender.api.Sender;
import org.incendo.cloud.context.CommandContext;

public class RequirementEvaluationService {
  public boolean evaluate(SenderRequirement requirement, CommandContext<Sender> context) {
    return requirement.evaluateRequirement(context);
  }

  public boolean isPlayer(CommandContext<Sender> context) {
    return context.sender().isPlayer();
  }
}
