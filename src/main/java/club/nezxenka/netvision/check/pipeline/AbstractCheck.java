package club.nezxenka.netvision.check.pipeline;

import club.nezxenka.netvision.check.api.Check;
import club.nezxenka.netvision.check.metadata.CheckData;
import club.nezxenka.netvision.player.model.NetVisionPlayer;
import lombok.Getter;

@Getter
public abstract class AbstractCheck implements Check {
  protected final NetVisionPlayer nvPlayer;
  private final String checkName;
  private final String configName;

  public AbstractCheck(NetVisionPlayer nvPlayer) {
    this.nvPlayer = nvPlayer;
    CheckData data = getClass().getAnnotation(CheckData.class);
    this.checkName = data.name();
    this.configName = data.configName().equals("DEFAULT") ? data.name() : data.configName();
  }

  protected void flag(String debug) {
    nvPlayer.getPunishmentManager().handleFlag(this, debug);
  }
}
