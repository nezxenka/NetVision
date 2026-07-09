package club.nezxenka.netvision.data;

import club.nezxenka.netvision.check.rotation.internal.AimProcessor;
import club.nezxenka.netvision.player.model.NetVisionPlayer;

public class TickData {
  public final float deltaYaw, deltaPitch;
  public final float accelYaw, accelPitch;
  public final float jerkPitch, jerkYaw;
  public final float gcdErrorYaw, gcdErrorPitch;

  public TickData(NetVisionPlayer nvPlayer) {
    AimProcessor aimProcessor = nvPlayer.getCheckManager().getCheck(AimProcessor.class);
    this.deltaYaw = nvPlayer.yaw - nvPlayer.lastYaw;
    this.deltaPitch = nvPlayer.pitch - nvPlayer.lastPitch;
    this.accelYaw = aimProcessor.getCurrentYawAccel();
    this.accelPitch = aimProcessor.getCurrentPitchAccel();
    this.jerkYaw = this.accelYaw - aimProcessor.getLastYawAccel();
    this.jerkPitch = this.accelPitch - aimProcessor.getLastPitchAccel();
    if (aimProcessor.getModeX() > 0) {
      double errorX = Math.abs(this.deltaYaw % aimProcessor.getModeX());
      this.gcdErrorYaw = (float) Math.min(errorX, aimProcessor.getModeX() - errorX);
    } else this.gcdErrorYaw = 0;
    if (aimProcessor.getModeY() > 0) {
      double errorY = Math.abs(this.deltaPitch % aimProcessor.getModeY());
      this.gcdErrorPitch = (float) Math.min(errorY, aimProcessor.getModeY() - errorY);
    } else this.gcdErrorPitch = 0;
  }
}
