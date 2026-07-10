package club.nezxenka.netvision.service.command.api;

import club.nezxenka.netvision.audience.api.Sender;
import net.kyori.adventure.text.Component;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.incendo.cloud.processors.requirements.Requirement;

public interface SenderRequirement extends Requirement<Sender, SenderRequirement> {
  @NonNull Component errorMessage(Sender sender);
}
