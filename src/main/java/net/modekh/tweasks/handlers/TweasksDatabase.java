package net.modekh.tweasks.handlers;

import net.modekh.tweasks.utils.Task;
import org.bukkit.entity.Player;

import java.sql.*;
import java.util.*;

public class TweasksDatabase {
    private final Connection connection;

    public TweasksDatabase(String path) throws SQLException {
        connection = DriverManager.getConnection("jdbc:sqlite:" + path);

        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS tweasks (" +
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

    public void addPlayerData(Player player) {
        try (PreparedStatement statement = connection.prepareStatement(
                "INSERT INTO tweasks (uuid, score, tasks) VALUES (?, 2, '')")) {
            statement.setString(1, player.getUniqueId().toString());
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int getPlayerScore(Player player) {
        try (PreparedStatement preparedStatement = connection.prepareStatement(
                "SELECT score FROM tweasks WHERE uuid = ?")) {
            preparedStatement.setString(1, player.getUniqueId().toString());
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                return resultSet.getInt("score");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 2; // initial score for all
    }

    public boolean addCompletedTask(Player player, Task task) throws SQLException {
        Set<Integer> completedTasks = getCompletedTasks(player);

        System.out.println("TASKS: " + completedTasks);

        if (!completedTasks.add(task.ordinal()))
            return false;

        int newScore = getPlayerScore(player) + task.getReward();
        String taskString = String.join(",", completedTasks.stream().map(String::valueOf).toList());

        try (PreparedStatement statement = connection.prepareStatement("UPDATE tweasks " +
                        "SET score = ?, tasks = ? WHERE uuid = ?")) {
            statement.setInt(1, newScore);
            statement.setString(2, taskString);
            statement.setString(3, player.getUniqueId().toString());
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return true;
    }

    public Set<Integer> getCompletedTasks(Player player) {
        Set<Integer> completedTasks = new HashSet<>();

        try (PreparedStatement statement = connection.prepareStatement("SELECT tasks FROM tweasks WHERE uuid = ?")) {
            statement.setString(1, player.getUniqueId().toString());
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                String tasks = resultSet.getString("tasks");

                if (tasks != null && !tasks.isEmpty()) {
                    for (String task : tasks.split(",")) {
                        completedTasks.add(Integer.parseInt(task));
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return completedTasks;
    }

    public void resetDatabase() {
        try (Statement stmt = connection.createStatement()) {
            stmt.executeUpdate("DELETE FROM tweasks");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
