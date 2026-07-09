package club.nezxenka.netvision.database.sqlite.query;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.UUID;

public class SqlParameterBinder {
  public void bindUuid(PreparedStatement ps, int index, UUID uuid) throws SQLException {
    ps.setString(index, uuid.toString());
  }

  public void bindString(PreparedStatement ps, int index, String value) throws SQLException {
    ps.setString(index, value);
  }

  public void bindInt(PreparedStatement ps, int index, int value) throws SQLException {
    ps.setInt(index, value);
  }

  public void bindLong(PreparedStatement ps, int index, long value) throws SQLException {
    ps.setLong(index, value);
  }

  public void bindDouble(PreparedStatement ps, int index, double value) throws SQLException {
    ps.setDouble(index, value);
  }
}
