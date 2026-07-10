package club.nezxenka.netvision.engine.api;

import club.nezxenka.netvision.util.rotation.RotationUpdate;

public interface AimModule extends AnalysisModule {
  void process(RotationUpdate rotationUpdate);
}
