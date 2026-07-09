package club.nezxenka.netvision.check.rotation.internal.gcd;

import club.nezxenka.netvision.util.math.NetVisionMath;

public class GcdResolver {
  public double resolve(double current, double previous) {
    double absCurrent = Math.abs(current);
    if (absCurrent <= 0 || absCurrent >= 5) return 0;
    double gcd = NetVisionMath.gcd(absCurrent, previous);
    return gcd > NetVisionMath.MINIMUM_DIVISOR ? gcd : 0;
  }
}
