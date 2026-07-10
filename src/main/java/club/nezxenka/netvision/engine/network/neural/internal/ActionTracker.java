package club.nezxenka.netvision.engine.network.neural;

import club.nezxenka.netvision.actor.model.NetVisionPlayer;
import club.nezxenka.netvision.core.config.ConfigManager;
import club.nezxenka.netvision.engine.api.PacketModule;
import club.nezxenka.netvision.engine.base.BaseModule;
import club.nezxenka.netvision.engine.model.ModuleInfo;
import club.nezxenka.netvision.entity.api.PacketEntity;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientInteractEntity;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerFlying;

@ModuleInfo(name = "ActionManager_Internal")
public class ActionTracker extends BaseModule implements PacketModule {
  public ActionTracker(NetVisionPlayer player, ConfigManager configManager) {
    super(player);
    int sequence = configManager.getAiSequence();
    player.ticksSinceAttack = sequence + 1;
  }

  @Override
  public void onPacketReceive(final PacketReceiveEvent event) {
    if (event.getPacketType() == PacketType.Play.Client.INTERACT_ENTITY) {
      WrapperPlayClientInteractEntity action = new WrapperPlayClientInteractEntity(event);
      if (action.getAction() == WrapperPlayClientInteractEntity.InteractAction.ATTACK) {
        PacketEntity entity = nvPlayer.getCompensatedEntities().getEntity(action.getEntityId());
        if (entity == null || entity.isPlayer) nvPlayer.ticksSinceAttack = 0;
      }
    } else if (WrapperPlayClientPlayerFlying.isFlying(event.getPacketType())) {
      nvPlayer.ticksSinceAttack++;
    }
  }
}
