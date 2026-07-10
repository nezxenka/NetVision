package club.nezxenka.netvision.service.enforce.model;

import club.nezxenka.netvision.engine.api.AnalysisModule;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.NavigableMap;
import java.util.Set;
import lombok.Getter;

@Getter
public class EnforcementGroup {
  private final String groupName;
  private final Set<String> associatedCheckNames;
  private final NavigableMap<Integer, List<String>> actions;

  public EnforcementGroup(
      String groupName, List<String> moduleNames, NavigableMap<Integer, List<String>> actions) {
    this.groupName = groupName;
    this.associatedCheckNames =
        new HashSet<>(moduleNames.stream().map(String::toLowerCase).toList());
    this.actions = actions;
  }

  public boolean isModuleAssociated(AnalysisModule module) {
    String moduleNameLower = module.getModuleName().toLowerCase(Locale.ROOT);
    for (String filter : associatedCheckNames) if (moduleNameLower.contains(filter)) return true;
    return false;
  }
}
