package club.nezxenka.netvision.engine.aim.sensitivity;

public class MouseSensitivityEstimator {
  public double estimateFromDivisor(double divisor) {
    double var11 = divisor / 0.15F / 8.0D;
    double var9 = Math.cbrt(var11);
    return (var9 - 0.2f) / 0.6f;
  }

  public double toPercentage(double sensitivity) {
    return sensitivity * 200.0;
  }
}
