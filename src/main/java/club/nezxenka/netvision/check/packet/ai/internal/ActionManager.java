package club.nezxenka.netvision.check.packet.ai.internal;

import club.nezxenka.netvision.check.api.PacketCheck;
import club.nezxenka.netvision.check.metadata.CheckData;
import club.nezxenka.netvision.check.pipeline.AbstractCheck;
import club.nezxenka.netvision.config.core.ConfigManager;
import club.nezxenka.netvision.entity.api.PacketEntity;
import club.nezxenka.netvision.player.model.NetVisionPlayer;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientInteractEntity;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerFlying;

@CheckData(name = "ActionManager_Internal")
public class ActionManager extends AbstractCheck implements PacketCheck {
  public ActionManager(NetVisionPlayer player, ConfigManager configManager) {
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
