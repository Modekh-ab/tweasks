package net.modekh.tweasks.db;

import net.modekh.tweasks.utils.Task;
import org.bukkit.entity.Player;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PlayersDatabase {
    private final Connection connection;

    public PlayersDatabase(String path) throws SQLException {
        connection = DriverManager.getConnection("jdbc:sqlite:" + path);

        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS players (" +
                    "uuid TEXT PRIMARY KEY, " +
                    "score INTEGER NOT NULL DEFAULT 2, " +
                    "tasks TEXT)");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void closeConnection() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }

    public void addPlayer(Player player) throws SQLException {
        // error if the player already exists
        try (PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO players (uuid) VALUES (?)")) {
            preparedStatement.setString(1, player.getUniqueId().toString());
            preparedStatement.executeUpdate();
        }
    }

    public boolean playerExists(Player player) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM players WHERE uuid = ?")) {
            preparedStatement.setString(1, player.getUniqueId().toString());
            ResultSet resultSet = preparedStatement.executeQuery();
            return resultSet.next();
        }
    }

    public void updatePlayerData(Player player, int score, List<Task> tasks) throws SQLException{
        if (!playerExists(player)){
            addPlayer(player);
        }

        try (PreparedStatement preparedStatement = connection.prepareStatement("UPDATE players SET score = ? WHERE uuid = ?")) {
            preparedStatement.setInt(1, score);
            preparedStatement.setString(2, player.getUniqueId().toString());
            preparedStatement.executeUpdate();
        }

        try (PreparedStatement preparedStatement = connection.prepareStatement("UPDATE players SET tasks = ? WHERE uuid = ?")) {
            preparedStatement.setString(1, getTasksString(tasks));
            preparedStatement.setString(2, player.getUniqueId().toString());
            preparedStatement.executeUpdate();
        }
    }

    public int getPlayerScore(Player player) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement("SELECT score FROM players WHERE uuid = ?")) {
            preparedStatement.setString(1, player.getUniqueId().toString());
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                return resultSet.getInt("score");
            } else {
                return 2; // initial score for all
            }
        }
    }

    public List<Task> getPlayerTasks(Player player) throws SQLException {
        List<Task> tasks = new ArrayList<>();

        try (PreparedStatement preparedStatement = connection.prepareStatement("SELECT tasks FROM players WHERE uuid = ?")) {
            preparedStatement.setString(1, player.getUniqueId().toString());
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                String tasksString = resultSet.getString("tasks");

                if (tasksString != null && !tasksString.isEmpty()) {
                    for (String ordinal : tasksString.split(",")) {
                        tasks.add(Task.values()[Integer.parseInt(ordinal)]);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return tasks;
    }

    public void savePlayerTasks(String playerId, List<Task> tasks) {
        String tasksOrds = getTasksString(tasks);

        try (PreparedStatement ps = connection.prepareStatement("INSERT OR REPLACE INTO players (uuid, tasks) VALUES (?, ?)")) {
            ps.setString(1, playerId);
            ps.setString(2, tasksOrds);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void resetPlayerTasks(String playerId) {
        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate("DELETE FROM players");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static String getTasksString(List<Task> tasks) {
        return String.join(",", tasks.stream().map(task -> String.valueOf(task.ordinal())).toList());
    }
}
