package club.nezxenka.netvision.database.api.query;

public class QueryBuilder {
  public String selectWhere(String table, String column) {
    return "SELECT * FROM "
        + table
        + " WHERE "
        + column
        + " = ? ORDER BY created_at DESC LIMIT ? OFFSET ?";
  }

  public String countWhere(String table, String column) {
    return "SELECT COUNT(*) FROM " + table + " WHERE " + column + " = ?";
  }

  public String deleteWhere(String table, String column) {
    return "DELETE FROM " + table + " WHERE " + column + " = ?";
  }

  public String insertValues(String table, int placeholders) {
    StringBuilder sb = new StringBuilder("INSERT INTO ").append(table).append(" VALUES (");
    for (int i = 0; i < placeholders; i++) {
      if (i > 0) sb.append(", ");
      sb.append("?");
    }
    sb.append(")");
    return sb.toString();
  }
}
