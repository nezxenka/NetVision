package club.nezxenka.netvision.engine.api.context;

import club.nezxenka.netvision.util.rotation.RotationUpdate;

public interface RotationProcessorSpec {
  void onRotation(RotationUpdate update);

  boolean isActive();
}
