package club.nezxenka.netvision.util.math.gcd;

public class GcdComputer {
  private static final double MINIMUM = ((Math.pow(0.2f, 3) * 8) * 0.15) - 1e-3;

  public double compute(double a, double b) {
    if (a == 0) return 0;
    if (a < b) {
      double t = a;
      a = b;
      b = t;
    }
    while (b > MINIMUM) {
      double t = a - (Math.floor(a / b) * b);
      a = b;
      b = t;
    }
    return a;
  }
}
