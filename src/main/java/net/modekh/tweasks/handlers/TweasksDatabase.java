package net.modekh.tweasks.handlers;

import net.modekh.tweasks.utils.Task;
import org.bukkit.Material;
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
                    "tasks TEXT, item TEXT, " +
                    "time_sailed INTEGER NOT NULL DEFAULT 0, " +
                    "deaths INTEGER NOT NULL DEFAULT 0)");
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
                "INSERT INTO tweasks (uuid, score, tasks, item, time_sailed, deaths) " +
                        "VALUES (?, 2, '', '', 0, 0)")) {
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
            return false;
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

    public boolean addItemToGuess(Player player, String itemId) {
        if (getMaterial(itemId) == null)
            return false;

        try (PreparedStatement statement = connection.prepareStatement("UPDATE tweasks " +
                "SET item = ? WHERE uuid = ?")) {
            statement.setString(1, itemId);
            statement.setString(2, player.getUniqueId().toString());
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public boolean guessItem(Player opponent, String itemId) {
        String opponentItemId = getPlayerItem(opponent);

        if (getMaterial(itemId) == null || opponentItemId == null || opponentItemId.isEmpty())
            return false;

        try (PreparedStatement statement = connection.prepareStatement(
                "SELECT COUNT(*) FROM tweasks WHERE uuid = ? AND item = ?")) {
            statement.setString(1, opponent.getUniqueId().toString());
            statement.setString(2, itemId);

            ResultSet resultSet = statement.executeQuery();
            return resultSet.next() && resultSet.getInt(1) > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public String getPlayerItem(Player player) {
        try (PreparedStatement statement = connection.prepareStatement(
                "SELECT item FROM tweasks WHERE uuid = ?")) {
            statement.setString(1, player.getUniqueId().toString());
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                return resultSet.getString("item");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return "";
    }

    public boolean setPlayerTimeSailed(Player player, int time) {
        try (PreparedStatement statement = connection.prepareStatement("UPDATE tweasks " +
                "SET time_sailed = ? WHERE uuid = ?")) {
            statement.setInt(1, time);
            statement.setString(2, player.getUniqueId().toString());
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public int getPlayerTimeSailed(Player player) {
        try (PreparedStatement preparedStatement = connection.prepareStatement(
                "SELECT time_sailed FROM tweasks WHERE uuid = ?")) {
            preparedStatement.setString(1, player.getUniqueId().toString());
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                return resultSet.getInt("time_sailed");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 0;
    }

    public boolean addPlayerDeath(Player player) {
        try (PreparedStatement statement = connection.prepareStatement("UPDATE tweasks " +
                "SET deaths = ? WHERE uuid = ?")) {
            statement.setInt(1, getPlayerDeaths(player) + 1);
            statement.setString(2, player.getUniqueId().toString());
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

        return getPlayerDeaths(player) > 4;
    }

    public int getPlayerDeaths(Player player) {
        try (PreparedStatement preparedStatement = connection.prepareStatement(
                "SELECT deaths FROM tweasks WHERE uuid = ?")) {
            preparedStatement.setString(1, player.getUniqueId().toString());
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                return resultSet.getInt("deaths");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 0;
    }

    public void resetDatabase() {
        try (Statement stmt = connection.createStatement()) {
            stmt.executeUpdate("DELETE FROM tweasks");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static Material getMaterial(String itemId) {
        itemId = "minecraft:" + itemId;
        return Material.matchMaterial(itemId);
    }
}
