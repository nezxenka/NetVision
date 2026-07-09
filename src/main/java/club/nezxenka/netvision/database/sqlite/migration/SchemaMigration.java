package club.nezxenka.netvision.database.sqlite.migration;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class SchemaMigration {
  private final List<String> statements = new ArrayList<>();

  public SchemaMigration add(String sql) {
    statements.add(sql);
    return this;
  }

  public void execute(Connection connection) throws SQLException {
    try (Statement stmt = connection.createStatement()) {
      for (String sql : statements) stmt.execute(sql);
    }
  }
}
