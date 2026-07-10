package club.nezxenka.netvision.engine.base;

import club.nezxenka.netvision.actor.model.NetVisionPlayer;
import club.nezxenka.netvision.engine.api.AnalysisModule;
import club.nezxenka.netvision.engine.model.ModuleInfo;
import lombok.Getter;

@Getter
public abstract class BaseModule implements AnalysisModule {
  protected final NetVisionPlayer nvPlayer;
  private final String moduleName;
  private final String configName;

  public BaseModule(NetVisionPlayer nvPlayer) {
    this.nvPlayer = nvPlayer;
    ModuleInfo data = getClass().getAnnotation(ModuleInfo.class);
    this.moduleName = data.name();
    this.configName = data.configName().equals("DEFAULT") ? data.name() : data.configName();
  }

  protected void flag(String debug) {
    nvPlayer.getEnforcementManager().handleFlag(this, debug);
  }
}
