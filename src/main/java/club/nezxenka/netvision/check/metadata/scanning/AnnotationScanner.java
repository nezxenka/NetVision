package club.nezxenka.netvision.check.metadata.scanning;

import club.nezxenka.netvision.check.metadata.CheckData;

public class AnnotationScanner {
  public CheckData scan(Class<?> checkClass) {
    return checkClass.getAnnotation(CheckData.class);
  }
}
