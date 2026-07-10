package club.nezxenka.netvision.engine.model;

public class AnnotationScanner {
  public ModuleInfo scan(Class<?> checkClass) {
    return checkClass.getAnnotation(ModuleInfo.class);
  }
}
