package club.nezxenka.netvision.core.storage.model.mapper;

import club.nezxenka.netvision.core.storage.model.Infraction;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class InfractionResultSetMapper {

  public List<Infraction> mapAll(ResultSet rs) throws SQLException {
    List<Infraction> violations = new ArrayList<>();
    while (rs.next()) {
      violations.add(
          new Infraction(
              rs.getString("server"),
              UUID.fromString(rs.getString("uuid")),
              rs.getString("player_name"),
              rs.getString("check_name"),
              rs.getString("verbose"),
              rs.getInt("vl"),
              Instant.ofEpochMilli(rs.getLong("created_at"))));
    }
    return violations;
  }
}
