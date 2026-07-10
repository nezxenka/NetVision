package club.nezxenka.netvision.engine.network.misc;

import club.nezxenka.netvision.actor.model.NetVisionPlayer;
import club.nezxenka.netvision.core.config.ConfigManager;
import club.nezxenka.netvision.engine.api.PacketModule;
import club.nezxenka.netvision.engine.base.BaseModule;
import club.nezxenka.netvision.engine.model.ModuleInfo;
import club.nezxenka.netvision.service.signal.internal.SignalManager;
import club.nezxenka.netvision.service.signal.model.SignalType;
import club.nezxenka.netvision.util.chat.ChatUtil;
import club.nezxenka.netvision.util.message.Message;
import club.nezxenka.netvision.util.message.MessageUtil;
import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.manager.server.ServerVersion;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.protocol.player.ClientVersion;
import com.github.retrooper.packetevents.wrapper.configuration.client.WrapperConfigClientPluginMessage;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPluginMessage;
import java.nio.charset.StandardCharsets;
import lombok.Getter;
import net.kyori.adventure.text.Component;

@ModuleInfo(name = "ClientBrand_Internal")
public class BrandScanner extends BaseModule implements PacketModule {

  private static final String CHANNEL =
      PacketEvents.getAPI()
              .getServerManager()
              .getVersion()
              .isNewerThanOrEquals(ServerVersion.V_1_13)
          ? "minecraft:brand"
          : "MC|Brand";
  private final ConfigManager configManager;
  private final SignalManager alertManager;

  @Getter private String brand = "vanilla";

  private boolean hasBrand = false;

  public BrandScanner(
      NetVisionPlayer player, ConfigManager configManager, SignalManager alertManager) {
    super(player);
    this.configManager = configManager;
    this.alertManager = alertManager;
  }

  @Override
  public void onPacketReceive(final PacketReceiveEvent event) {
    if (event.getPacketType() == PacketType.Play.Client.PLUGIN_MESSAGE) {
      WrapperPlayClientPluginMessage packet = new WrapperPlayClientPluginMessage(event);
      handle(packet.getChannelName(), packet.getData());
    } else if (event.getPacketType() == PacketType.Configuration.Client.PLUGIN_MESSAGE) {
      WrapperConfigClientPluginMessage packet = new WrapperConfigClientPluginMessage(event);
      handle(packet.getChannelName(), packet.getData());
    }
  }

  private void handle(String channel, byte[] data) {
    if (!channel.equals(BrandScanner.CHANNEL) || hasBrand) return;
    hasBrand = true;
    if (data.length > 64 || data.length == 0) {
      brand = "invalid (" + data.length + " bytes)";
    } else {
      byte[] brandBytes = new byte[data.length - 1];
      System.arraycopy(data, 1, brandBytes, 0, brandBytes.length);
      brand = new String(brandBytes, StandardCharsets.UTF_8).replace(" (Velocity)", "");
      brand = ChatUtil.stripColor(brand);
    }
    nvPlayer.setBrand(brand);
    if (!configManager.isClientIgnored(brand)) {
      Component component =
          MessageUtil.getMessage(
              Message.BRAND_NOTIFICATION, "player", nvPlayer.getPlayer().getName(), "brand", brand);
      alertManager.send(component, SignalType.BRAND);
    }
    final boolean hasReachExploit =
        brand.contains("forge")
            && nvPlayer.getUser().getClientVersion().isNewerThanOrEquals(ClientVersion.V_1_18_2)
            && nvPlayer.getUser().getClientVersion().isOlderThan(ClientVersion.V_1_19_4);
    if (hasReachExploit && configManager.isDisconnectBlacklistedForge())
      nvPlayer.disconnect(MessageUtil.getMessage(Message.BRAND_DISCONNECT_FORGE));
  }
}
