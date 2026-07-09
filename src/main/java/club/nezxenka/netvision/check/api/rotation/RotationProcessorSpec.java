package club.nezxenka.netvision.check.api.rotation;

import club.nezxenka.netvision.util.rotation.RotationUpdate;

public interface RotationProcessorSpec {
  void onRotation(RotationUpdate update);

  boolean isActive();
}
