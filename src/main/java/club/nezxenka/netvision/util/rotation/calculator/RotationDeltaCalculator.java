package club.nezxenka.netvision.util.rotation.calculator;

public class RotationDeltaCalculator {
  public float yawDelta(float current, float previous) {
    return current - previous;
  }

  public float pitchDelta(float current, float previous) {
    return current - previous;
  }

  public float normalizeYaw(float yaw) {
    yaw = yaw % 360;
    if (yaw > 180) yaw -= 360;
    if (yaw < -180) yaw += 360;
    return yaw;
  }
}
