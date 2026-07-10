package club.nezxenka.netvision.engine.aim;

import club.nezxenka.netvision.actor.model.NetVisionPlayer;
import club.nezxenka.netvision.engine.api.AimModule;
import club.nezxenka.netvision.engine.base.BaseModule;
import club.nezxenka.netvision.engine.model.ModuleInfo;
import club.nezxenka.netvision.util.collection.Pair;
import club.nezxenka.netvision.util.collection.RunningMode;
import club.nezxenka.netvision.util.math.NetVisionMath;
import club.nezxenka.netvision.util.rotation.RotationUpdate;
import lombok.Getter;

@ModuleInfo(name = "AimProcessor_Internal")
@Getter
public class AimEvaluator extends BaseModule implements AimModule {
  private static final int SIGNIFICANT_SAMPLES_THRESHOLD = 15;
  private static final int TOTAL_SAMPLES_THRESHOLD = 80;
  public double sensitivityX;
  public double sensitivityY;
  public double divisorX;
  public double divisorY;
  public double modeX, modeY;
  public double deltaDotsX, deltaDotsY;
  private final RunningMode xRotMode = new RunningMode(TOTAL_SAMPLES_THRESHOLD);
  private final RunningMode yRotMode = new RunningMode(TOTAL_SAMPLES_THRESHOLD);
  private float lastXRot;
  private float lastYRot;
  private float lastDeltaYaw = 0.0f;
  private float lastDeltaPitch = 0.0f;
  private float lastYawAccel = 0.0f;
  private float lastPitchAccel = 0.0f;
  private float currentYawAccel = 0.0f;
  private float currentPitchAccel = 0.0f;

  public AimEvaluator(NetVisionPlayer nvPlayer) {
    super(nvPlayer);
  }

  public static double convertToSensitivity(double var13) {
    double var11 = var13 / 0.15F / 8.0D;
    double var9 = Math.cbrt(var11);
    return (var9 - 0.2f) / 0.6f;
  }

  @Override
  public void process(final RotationUpdate rotationUpdate) {
    float deltaYaw = rotationUpdate.getDeltaYaw();
    float deltaPitch = rotationUpdate.getDeltaPitch();
    float deltaYawAbs = Math.abs(deltaYaw);
    float deltaPitchAbs = Math.abs(deltaPitch);
    this.lastYawAccel = this.currentYawAccel;
    this.lastPitchAccel = this.currentPitchAccel;
    this.currentYawAccel = deltaYawAbs - Math.abs(this.lastDeltaYaw);
    this.currentPitchAccel = deltaPitchAbs - Math.abs(this.lastDeltaPitch);
    this.lastDeltaYaw = deltaYaw;
    this.lastDeltaPitch = deltaPitch;
    this.divisorX = NetVisionMath.gcd(deltaYawAbs, lastXRot);
    if (deltaYawAbs > 0 && deltaYawAbs < 5 && divisorX > NetVisionMath.MINIMUM_DIVISOR) {
      this.xRotMode.add(divisorX);
      this.lastXRot = deltaYawAbs;
    }
    this.divisorY = NetVisionMath.gcd(deltaPitchAbs, lastYRot);
    if (deltaPitchAbs > 0 && deltaPitchAbs < 5 && divisorY > NetVisionMath.MINIMUM_DIVISOR) {
      this.yRotMode.add(divisorY);
      this.lastYRot = deltaPitchAbs;
    }
    if (this.xRotMode.size() > SIGNIFICANT_SAMPLES_THRESHOLD) {
      Pair<Double, Integer> modeResult = this.xRotMode.getMode();
      if (modeResult.second() > SIGNIFICANT_SAMPLES_THRESHOLD) {
        this.modeX = modeResult.first();
        this.sensitivityX = convertToSensitivity(this.modeX);
      }
    }
    if (this.yRotMode.size() > SIGNIFICANT_SAMPLES_THRESHOLD) {
      Pair<Double, Integer> modeResult = this.yRotMode.getMode();
      if (modeResult.second() > SIGNIFICANT_SAMPLES_THRESHOLD) {
        this.modeY = modeResult.first();
        this.sensitivityY = convertToSensitivity(this.modeY);
      }
    }
    if (modeX > 0) this.deltaDotsX = deltaYawAbs / modeX;
    if (modeY > 0) this.deltaDotsY = deltaPitchAbs / modeY;
  }
}
