package club.nezxenka.netvision.command.impl.ban.executor;

import org.bukkit.Bukkit;

public class BanCommandExecutor {
  public void execute(String targetName) {
    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "shame ban " + targetName);
  }
}
