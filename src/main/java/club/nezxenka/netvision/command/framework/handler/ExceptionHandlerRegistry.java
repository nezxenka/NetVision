package club.nezxenka.netvision.command.framework.handler;

import club.nezxenka.netvision.sender.api.Sender;
import java.util.function.Function;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.format.NamedTextColor;
import org.incendo.cloud.CommandManager;

public class ExceptionHandlerRegistry {
  public <E extends Exception> void register(
      CommandManager<Sender> manager, Class<E> exceptionType, Function<E, ComponentLike> mapper) {
    manager
        .exceptionController()
        .registerHandler(
            exceptionType,
            ctx ->
                ctx.context()
                    .sender()
                    .sendMessage(
                        mapper
                            .apply(ctx.exception())
                            .asComponent()
                            .colorIfAbsent(NamedTextColor.RED)));
  }
}
