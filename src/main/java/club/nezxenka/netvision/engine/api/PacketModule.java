package club.nezxenka.netvision.engine.api;

import com.github.retrooper.packetevents.event.PacketReceiveEvent;

public interface PacketModule extends AnalysisModule {
  default void onPacketReceive(PacketReceiveEvent event) {}
}
