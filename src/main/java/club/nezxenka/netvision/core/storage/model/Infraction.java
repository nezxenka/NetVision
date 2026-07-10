package club.nezxenka.netvision.core.storage.model;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public record Infraction(
    String serverName,
    UUID playerUUID,
    String playerName,
    String moduleName,
    String verbose,
    int vl,
    Instant createdAt) {
  public static List<Infraction> fromResultSet(ResultSet resultSet) throws SQLException {
    List<Infraction> violations = new ArrayList<>();
    while (resultSet.next()) {
      String server = resultSet.getString("server");
      UUID player = UUID.fromString(resultSet.getString("uuid"));
      String playerName = resultSet.getString("player_name");
      String moduleName = resultSet.getString("check_name");
      String verbose = resultSet.getString("verbose");
      int vl = resultSet.getInt("vl");
      Instant createdAt = Instant.ofEpochMilli(resultSet.getLong("created_at"));
      violations.add(
          new Infraction(server, player, playerName, moduleName, verbose, vl, createdAt));
    }
    return violations;
  }
}
