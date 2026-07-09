package club.nezxenka.netvision.util.math.vector;

import com.github.retrooper.packetevents.util.Vector3d;

public class VectorUtils {
  public double distanceSquared(Vector3d a, Vector3d b) {
    return a.distanceSquared(b);
  }

  public boolean isNear(double actual, double expected, double epsilon) {
    return Math.abs(actual - expected) < epsilon;
  }

  public double epsilon() {
    return 1.0E-7;
  }
}
