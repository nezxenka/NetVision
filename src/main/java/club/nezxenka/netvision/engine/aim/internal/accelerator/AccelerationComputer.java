package club.nezxenka.netvision.engine.aim.accelerator;

public class AccelerationComputer {
  private float lastDeltaYaw;
  private float lastDeltaPitch;
  private float currentYawAccel;
  private float currentPitchAccel;
  private float lastYawAccel;
  private float lastPitchAccel;

  public void tick(float deltaYaw, float deltaPitch) {
    this.lastYawAccel = this.currentYawAccel;
    this.lastPitchAccel = this.currentPitchAccel;
    this.currentYawAccel = Math.abs(deltaYaw) - Math.abs(this.lastDeltaYaw);
    this.currentPitchAccel = Math.abs(deltaPitch) - Math.abs(this.lastDeltaPitch);
    this.lastDeltaYaw = deltaYaw;
    this.lastDeltaPitch = deltaPitch;
  }

  public float getCurrentYawAccel() {
    return currentYawAccel;
  }

  public float getCurrentPitchAccel() {
    return currentPitchAccel;
  }

  public float getLastYawAccel() {
    return lastYawAccel;
  }

  public float getLastPitchAccel() {
    return lastPitchAccel;
  }
}
