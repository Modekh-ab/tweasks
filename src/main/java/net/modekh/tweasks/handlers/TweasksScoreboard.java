package net.modekh.tweasks.handlers;

import net.modekh.tweasks.Tweasks;
import net.modekh.tweasks.utils.messages.ChatUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

import java.sql.SQLException;

public class TweasksScoreboard {
    private final Tweasks main;
    private final Scoreboard board;

    public TweasksScoreboard(Tweasks main) {
        this.main = main;
        this.board = Bukkit.getScoreboardManager().getNewScoreboard();

        Objective objective = board.registerNewObjective("tasks", "dummy");
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
    }

    public void addPlayerToScoreboard(Player player) throws SQLException {
        Objective objective = board.getObjective("tasks");

        if (objective == null)
            return;

        objective.setDisplaySlot(DisplaySlot.SIDEBAR);

        objective.setDisplayName(ChatUtils.formatMessage("&l&a", "Score"));
        player.setScoreboard(board);
        updateScore(player, 0);
    }

    public void updateScore(Player player, int reward) throws SQLException {
        Objective objective = board.getObjective("tasks");

        if (objective == null)
            return;

        int newScore = main.getDatabase().getPlayerScore(player) + reward;
        Score score = objective.getScore(player.getName());
        score.setScore(newScore);
    }

    public void resetScoreboard() {
        board.clearSlot(DisplaySlot.SIDEBAR);

        for (Objective objective : board.getObjectives()) {
            objective.setDisplayName("");
        }

        for (String entry : board.getEntries()) {
            board.resetScores(entry);
        }
    }
}
