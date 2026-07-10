package club.nezxenka.netvision.service.command.impl.reload.executor;

import club.nezxenka.netvision.NetVision;

public class PluginReloadTask {
  private final NetVision plugin;

  public PluginReloadTask(NetVision plugin) {
    this.plugin = plugin;
  }

  public void execute() {
    plugin.reloadPlugin();
  }
}
