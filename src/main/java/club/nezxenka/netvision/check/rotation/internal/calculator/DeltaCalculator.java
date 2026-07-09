package club.nezxenka.netvision.check.rotation.internal.calculator;

public class DeltaCalculator {
  public float computeDeltaYaw(float currentYaw, float previousYaw) {
    return currentYaw - previousYaw;
  }

  public float computeDeltaPitch(float currentPitch, float previousPitch) {
    return currentPitch - previousPitch;
  }

  public float computeAbsoluteAcceleration(float currentDelta, float previousDelta) {
    return Math.abs(currentDelta) - Math.abs(previousDelta);
  }
}
