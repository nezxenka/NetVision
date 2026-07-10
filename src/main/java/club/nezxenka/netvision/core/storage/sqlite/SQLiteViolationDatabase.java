package club.nezxenka.netvision.core.storage.sqlite;

import club.nezxenka.netvision.NetVision;
import club.nezxenka.netvision.actor.model.NetVisionPlayer;
import club.nezxenka.netvision.core.config.ConfigManager;
import club.nezxenka.netvision.core.storage.api.RecordStorage;
import club.nezxenka.netvision.core.storage.model.Infraction;
import club.nezxenka.netvision.core.storage.model.PlayerMenuData;
import club.nezxenka.netvision.core.storage.model.ProbabilityEntry;
import com.zaxxer.hikari.HikariDataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;

public class SQLiteViolationDatabase implements RecordStorage {
  private final HikariDataSource dataSource;
  private final NetVision plugin;
  private final ConfigManager configManager;

  public SQLiteViolationDatabase(
      HikariDataSource dataSource, NetVision plugin, ConfigManager configManager) {
    this.dataSource = dataSource;
    this.plugin = plugin;
    this.configManager = configManager;
    initTables();
  }

  private void initTables() {
    try (Connection conn = dataSource.getConnection();
        Statement statement = conn.createStatement()) {
      statement.execute(
          "CREATE TABLE IF NOT EXISTS violations(id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, server VARCHAR(255) NOT NULL, uuid CHAR(36) NOT NULL, player_name TEXT NOT NULL, check_name TEXT NOT NULL, verbose TEXT NOT NULL, vl INTEGER NOT NULL, created_at BIGINT NOT NULL);");
      statement.execute("DROP INDEX IF EXISTS idx_violations_uuid;");
      statement.execute(
          "CREATE INDEX IF NOT EXISTS idx_violations_uuid_time ON violations(uuid, created_at DESC);");
      statement.execute(
          "CREATE INDEX IF NOT EXISTS idx_violations_time ON violations(created_at DESC);");
      statement.execute(
          "CREATE TABLE IF NOT EXISTS netvision_punishments (uuid CHAR(36) NOT NULL, punish_group VARCHAR(255) NOT NULL, vl INTEGER NOT NULL, PRIMARY KEY (uuid, punish_group));");
      statement.execute(
          "CREATE TABLE IF NOT EXISTS chicken_coop_probabilities (id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, uuid CHAR(36) NOT NULL, player_name TEXT NOT NULL, probability REAL NOT NULL, created_at BIGINT NOT NULL);");
      statement.execute(
          "CREATE INDEX IF NOT EXISTS idx_coop_uuid_time ON chicken_coop_probabilities(uuid, created_at DESC);");
    } catch (SQLException e) {
      plugin.getLogger().log(Level.SEVERE, "Failed to initialize database tables", e);
    }
  }

  @Override
  public void logAlert(NetVisionPlayer player, String verbose, String moduleName, int vls) {
    String sql =
        "INSERT INTO violations (server, uuid, player_name, check_name, verbose, vl, created_at) VALUES (?, ?, ?, ?, ?, ?, ?)";
    try (Connection conn = dataSource.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setString(1, configManager.getConfig().getString("history.server-name", "server"));
      ps.setString(2, player.getUuid().toString());
      ps.setString(3, player.getPlayer().getName());
      ps.setString(4, moduleName);
      ps.setString(5, verbose);
      ps.setInt(6, vls);
      ps.setLong(7, System.currentTimeMillis());
      ps.executeUpdate();
    } catch (SQLException e) {
      plugin.getLogger().log(Level.SEVERE, "Failed to log violation", e);
    }
  }

  @Override
  public int getLogCount(UUID player) {
    String sql = "SELECT COUNT(*) FROM violations WHERE uuid = ?";
    try (Connection conn = dataSource.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setString(1, player.toString());
      try (ResultSet rs = ps.executeQuery()) {
        if (rs.next()) return rs.getInt(1);
      }
    } catch (SQLException e) {
      plugin.getLogger().log(Level.SEVERE, "Failed to count violations", e);
    }
    return 0;
  }

  @Override
  public List<Infraction> getViolations(UUID player, int page, int limit) {
    String sql =
        "SELECT * FROM violations WHERE uuid = ? ORDER BY created_at DESC LIMIT ? OFFSET ?";
    try (Connection conn = dataSource.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setString(1, player.toString());
      ps.setInt(2, limit);
      ps.setInt(3, (page - 1) * limit);
      try (ResultSet rs = ps.executeQuery()) {
        return Infraction.fromResultSet(rs);
      }
    } catch (SQLException e) {
      plugin.getLogger().log(Level.SEVERE, "Failed to get violations", e);
    }
    return List.of();
  }

  @Override
  public int getLogCount(long since) {
    String sql = "SELECT COUNT(*) FROM violations" + (since > 0 ? " WHERE created_at >= ?" : "");
    try (Connection conn = dataSource.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql)) {
      if (since > 0) ps.setLong(1, since);
      try (ResultSet rs = ps.executeQuery()) {
        if (rs.next()) return rs.getInt(1);
      }
    } catch (SQLException e) {
      plugin.getLogger().log(Level.SEVERE, "Failed to count all violations", e);
    }
    return 0;
  }

  @Override
  public List<Infraction> getViolations(int page, int limit, long since) {
    String sql =
        "SELECT * FROM violations"
            + (since > 0 ? " WHERE created_at >= ?" : "")
            + " ORDER BY created_at DESC LIMIT ? OFFSET ?";
    try (Connection conn = dataSource.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql)) {
      int paramIndex = 1;
      if (since > 0) ps.setLong(paramIndex++, since);
      ps.setInt(paramIndex++, limit);
      ps.setInt(paramIndex, (page - 1) * limit);
      try (ResultSet rs = ps.executeQuery()) {
        return Infraction.fromResultSet(rs);
      }
    } catch (SQLException e) {
      plugin.getLogger().log(Level.SEVERE, "Failed to get all violations", e);
    }
    return List.of();
  }

  @Override
  public int getViolationLevel(UUID playerUUID, String punishGroupName) {
    String sql = "SELECT vl FROM netvision_punishments WHERE uuid = ? AND punish_group = ?";
    try (Connection conn = dataSource.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setString(1, playerUUID.toString());
      ps.setString(2, punishGroupName);
      try (ResultSet rs = ps.executeQuery()) {
        if (rs.next()) return rs.getInt("vl");
      }
    } catch (SQLException e) {
      plugin.getLogger().log(Level.SEVERE, "Failed to get violation level for " + playerUUID, e);
    }
    return 0;
  }

  @Override
  public int incrementViolationLevel(UUID playerUUID, String punishGroupName) {
    String upsertSQL =
        "INSERT INTO netvision_punishments (uuid, punish_group, vl) VALUES (?, ?, 1) ON CONFLICT(uuid, punish_group) DO UPDATE SET vl = vl + 1";
    String selectSQL = "SELECT vl FROM netvision_punishments WHERE uuid = ? AND punish_group = ?";
    try (Connection conn = dataSource.getConnection()) {
      conn.setAutoCommit(false);
      try {
        try (PreparedStatement psUpsert = conn.prepareStatement(upsertSQL)) {
          psUpsert.setString(1, playerUUID.toString());
          psUpsert.setString(2, punishGroupName);
          psUpsert.executeUpdate();
        }
        try (PreparedStatement psSelect = conn.prepareStatement(selectSQL)) {
          psSelect.setString(1, playerUUID.toString());
          psSelect.setString(2, punishGroupName);
          try (ResultSet rs = psSelect.executeQuery()) {
            if (rs.next()) {
              int newVl = rs.getInt(1);
              conn.commit();
              return newVl;
            }
          }
        }
        conn.rollback();
        return 0;
      } catch (SQLException e) {
        conn.rollback();
        plugin
            .getLogger()
            .log(Level.SEVERE, "Failed to increment violation level for " + playerUUID, e);
        return 0;
      }
    } catch (SQLException e) {
      plugin
          .getLogger()
          .log(
              Level.SEVERE,
              "Database connection error while incrementing violation level for " + playerUUID,
              e);
      return 0;
    }
  }

  @Override
  public int getUniqueViolatorsSince(long since) {
    String sql = "SELECT COUNT(DISTINCT uuid) FROM violations WHERE created_at >= ?";
    try (Connection conn = dataSource.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setLong(1, since);
      try (ResultSet rs = ps.executeQuery()) {
        if (rs.next()) return rs.getInt(1);
      }
    } catch (SQLException e) {
      plugin.getLogger().log(Level.SEVERE, "Failed to count unique violators", e);
    }
    return 0;
  }

  @Override
  public void resetViolationLevel(UUID playerUUID, String punishGroupName) {
    String sql = "DELETE FROM netvision_punishments WHERE uuid = ? AND punish_group = ?";
    try (Connection conn = dataSource.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setString(1, playerUUID.toString());
      ps.setString(2, punishGroupName);
      ps.executeUpdate();
    } catch (SQLException e) {
      plugin.getLogger().log(Level.SEVERE, "Failed to reset violation level for " + playerUUID, e);
    }
  }

  @Override
  public void resetAllViolationLevels(UUID playerUUID) {
    String sql = "DELETE FROM netvision_punishments WHERE uuid = ?";
    try (Connection conn = dataSource.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setString(1, playerUUID.toString());
      ps.executeUpdate();
    } catch (SQLException e) {
      plugin
          .getLogger()
          .log(Level.SEVERE, "Failed to reset all violation levels for " + playerUUID, e);
    }
  }

  @Override
  public void saveProbability(UUID uuid, String playerName, double probability) {
    String sql =
        "INSERT INTO chicken_coop_probabilities (uuid, player_name, probability, created_at) VALUES (?, ?, ?, ?)";
    try (Connection conn = dataSource.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setString(1, uuid.toString());
      ps.setString(2, playerName);
      ps.setDouble(3, probability);
      ps.setLong(4, System.currentTimeMillis());
      ps.executeUpdate();
    } catch (SQLException e) {
      plugin.getLogger().log(Level.SEVERE, "Failed to save probability for " + uuid, e);
    }
  }

  @Override
  public List<ProbabilityEntry> getPlayerProbabilityEntries(UUID uuid, int limit, int offset) {
    List<ProbabilityEntry> entries = new ArrayList<>();
    String serverName = configManager.getConfig().getString("cross-server.server-name", "local");
    String sql =
        "SELECT probability, created_at FROM chicken_coop_probabilities WHERE uuid = ? ORDER BY created_at DESC LIMIT ? OFFSET ?";
    try (Connection conn = dataSource.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setString(1, uuid.toString());
      ps.setInt(2, limit);
      ps.setInt(3, offset);
      try (ResultSet rs = ps.executeQuery()) {
        while (rs.next())
          entries.add(
              new ProbabilityEntry(
                  rs.getDouble("probability"), rs.getLong("created_at"), serverName));
      }
    } catch (SQLException e) {
      plugin.getLogger().log(Level.SEVERE, "Failed to get probability entries for " + uuid, e);
    }
    java.util.Collections.reverse(entries);
    return entries;
  }

  @Override
  public int getPlayerProbabilityCount(UUID uuid) {
    String sql = "SELECT COUNT(*) FROM chicken_coop_probabilities WHERE uuid = ?";
    try (Connection conn = dataSource.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setString(1, uuid.toString());
      try (ResultSet rs = ps.executeQuery()) {
        if (rs.next()) return rs.getInt(1);
      }
    } catch (SQLException e) {
      plugin.getLogger().log(Level.SEVERE, "Failed to count probabilities for " + uuid, e);
    }
    return 0;
  }

  @Override
  public List<Double> getPlayerProbabilities(UUID uuid, int limit) {
    List<Double> probabilities = new ArrayList<>();
    String sql =
        "SELECT probability FROM chicken_coop_probabilities WHERE uuid = ? ORDER BY created_at DESC LIMIT ?";
    try (Connection conn = dataSource.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setString(1, uuid.toString());
      ps.setInt(2, limit);
      try (ResultSet rs = ps.executeQuery()) {
        while (rs.next()) probabilities.add(rs.getDouble("probability"));
      }
    } catch (SQLException e) {
      plugin.getLogger().log(Level.SEVERE, "Failed to get probabilities for " + uuid, e);
    }
    java.util.Collections.reverse(probabilities);
    return probabilities;
  }

  @Override
  public void deletePlayerProbabilities(UUID uuid) {
    String sql = "DELETE FROM chicken_coop_probabilities WHERE uuid = ?";
    try (Connection conn = dataSource.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setString(1, uuid.toString());
      ps.executeUpdate();
    } catch (SQLException e) {
      plugin.getLogger().log(Level.SEVERE, "Failed to delete probabilities for " + uuid, e);
    }
  }

  @Override
  public Map<UUID, PlayerMenuData> getAllOnlinePlayerMenuData() {
    Map<UUID, PlayerMenuData> data = new HashMap<>();
    String sql =
        "SELECT uuid, player_name, probability, created_at FROM chicken_coop_probabilities ORDER BY uuid, created_at DESC";
    try (Connection conn = dataSource.getConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(sql)) {
      Map<UUID, List<Double>> tempMap = new HashMap<>();
      Map<UUID, String> nameMap = new HashMap<>();
      while (rs.next()) {
        UUID uuid = UUID.fromString(rs.getString("uuid"));
        String playerName = rs.getString("player_name");
        double probability = rs.getDouble("probability");
        tempMap.computeIfAbsent(uuid, k -> new ArrayList<>()).add(probability);
        nameMap.putIfAbsent(uuid, playerName);
      }
      for (Map.Entry<UUID, List<Double>> entry : tempMap.entrySet()) {
        UUID uuid = entry.getKey();
        List<Double> probs = entry.getValue();
        if (probs.size() > 10) probs = probs.subList(0, 10);
        java.util.Collections.reverse(probs);
        data.put(uuid, new PlayerMenuData(uuid, nameMap.get(uuid), probs));
      }
    } catch (SQLException e) {
      plugin.getLogger().log(Level.SEVERE, "Failed to get all menu data", e);
    }
    return data;
  }
}
