package club.nezxenka.netvision.check.api;

import com.github.retrooper.packetevents.event.PacketReceiveEvent;

public interface PacketCheck extends Check {
  default void onPacketReceive(PacketReceiveEvent event) {}
}
