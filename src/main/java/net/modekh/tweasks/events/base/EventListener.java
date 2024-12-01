package net.modekh.tweasks.events.base;

import net.modekh.tweasks.Tweasks;
import net.modekh.tweasks.commands.TweasksCommand;
import net.modekh.tweasks.utils.messages.ChatUtils;
import net.modekh.tweasks.utils.Task;
import net.modekh.tweasks.utils.messages.RewardMessage;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.event.Listener;
import org.bukkit.scoreboard.Objective;

import java.sql.SQLException;

public class EventListener implements Listener {
    // data
    private static RewardMessage message = null;

    // constructor

    private final Tweasks main;

    public EventListener(Tweasks main) {
        this.main = main;
    }

    // utils

    public void addScore(Player player, Task task, boolean feedback) throws SQLException {
        if (!TweasksCommand.getActivePlayers().contains(player.getUniqueId()))
            return;

        int reward = task.getReward();
        Objective objective = player.getScoreboard().getObjective("tasks");

        if (objective == null)
            return;

        if (main.getDatabase().addCompletedTask(player, task)) {
            // scoreboard
            int currentScore = objective.getScore(player.getName()).getScore();
            int newScore = currentScore + reward;

            objective.getScore(player.getDisplayName()).setScore(newScore);

            if (feedback)
                sendTaskFeedback(player, task);
        }
    }

    public void addScore(Player player, Task task) throws SQLException {
        addScore(player, task, true);
    }

    public static void sendTaskFeedback(Player player, Task task) {
        int reward = task.getReward();
        boolean positive = reward >= 0;

        // chat reward message
        if (positive) {
            message = RewardMessage.next(message);

            if (message != null)
                ChatUtils.sendServerMessage(player, message.get());

            player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0F);
        }

        player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_BREAK, 1.0f, 1.0F);
    }
}
