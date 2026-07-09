package club.nezxenka.netvision.check.api;

import club.nezxenka.netvision.util.rotation.RotationUpdate;

public interface RotationCheck extends Check {
  void process(RotationUpdate rotationUpdate);
}
