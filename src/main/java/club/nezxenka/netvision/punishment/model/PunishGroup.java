package club.nezxenka.netvision.punishment.model;

import club.nezxenka.netvision.check.api.Check;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.NavigableMap;
import java.util.Set;
import lombok.Getter;

@Getter
public class PunishGroup {
  private final String groupName;
  private final Set<String> associatedCheckNames;
  private final NavigableMap<Integer, List<String>> actions;

  public PunishGroup(
      String groupName, List<String> checkNames, NavigableMap<Integer, List<String>> actions) {
    this.groupName = groupName;
    this.associatedCheckNames =
        new HashSet<>(checkNames.stream().map(String::toLowerCase).toList());
    this.actions = actions;
  }

  public boolean isCheckAssociated(Check check) {
    String checkNameLower = check.getCheckName().toLowerCase(Locale.ROOT);
    for (String filter : associatedCheckNames) if (checkNameLower.contains(filter)) return true;
    return false;
  }
}
