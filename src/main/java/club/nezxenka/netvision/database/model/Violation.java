package club.nezxenka.netvision.database.model;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public record Violation(
    String serverName,
    UUID playerUUID,
    String playerName,
    String checkName,
    String verbose,
    int vl,
    Instant createdAt) {
  public static List<Violation> fromResultSet(ResultSet resultSet) throws SQLException {
    List<Violation> violations = new ArrayList<>();
    while (resultSet.next()) {
      String server = resultSet.getString("server");
      UUID player = UUID.fromString(resultSet.getString("uuid"));
      String playerName = resultSet.getString("player_name");
      String checkName = resultSet.getString("check_name");
      String verbose = resultSet.getString("verbose");
      int vl = resultSet.getInt("vl");
      Instant createdAt = Instant.ofEpochMilli(resultSet.getLong("created_at"));
      violations.add(new Violation(server, player, playerName, checkName, verbose, vl, createdAt));
    }
    return violations;
  }
}
