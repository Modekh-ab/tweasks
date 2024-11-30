package net.modekh.tweasks.commands;

import net.modekh.tweasks.Tweasks;
import net.modekh.tweasks.utils.messages.ChatUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;

import java.sql.SQLException;
import java.util.*;

public class TweasksCommand implements CommandExecutor {
    private final Tweasks main;

    public TweasksCommand(Tweasks main) {
        this.main = main;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player))
            return false;

        if (args.length == 0) {
            ChatUtils.sendServerMessage(player, ChatColor.GOLD + "Invalid command!");
            return false;
        }

        // commands w/o op requirement

        if (args[0].equalsIgnoreCase("item")) {
            if (args.length != 3) {
                ChatUtils.sendInvalidMessage(player);
                return false;
            }

            try {
                Player opponent = Bukkit.getPlayer(args[1]);
                String itemId = args[2];

                ChatUtils.sendServerMessage(player, "Your item for "
                        + ChatUtils.aquaMessage(opponent.getName()) + ChatUtils.reset(" is ")
                        + ChatUtils.aquaMessage(itemId) + ChatUtils.reset("."));

                return true;
            } catch (Exception e) {
                ChatUtils.sendInvalidMessage(player);
                return false;
            }
        }

        if (args[0].equalsIgnoreCase("guess")) {
            if (args.length != 2) {
                ChatUtils.sendInvalidMessage(player);
                return false;
            }
        }

        // op commands

        if (!sender.isOp()) {
            sender.sendMessage(ChatColor.GOLD + "You don't have permission to use this command!");
            return false;
        }

        if (args[0].equalsIgnoreCase("start")) {
            try {
                createScoreboard(player);

                return true;
            } catch (SQLException e) {
                e.printStackTrace();
                return false;
            }
        }

        if (args[0].equalsIgnoreCase("reset")) {
            try {
                reset(player);

                return true;
            } catch (SQLException e) {
                e.printStackTrace();
                return false;
            }
        }

        return false;
    }

    private void createScoreboard(Player player) throws SQLException {
        ScoreboardManager manager = Bukkit.getScoreboardManager();

        if (manager != null) {
            Scoreboard board = manager.getNewScoreboard();
            Objective objective = board.registerNewObjective("tasks", "dummy");

            objective.setDisplayName(ChatUtils.formatMessage("&l&a", "Score"));
            objective.setDisplaySlot(DisplaySlot.SIDEBAR);

            for (Player p : Bukkit.getOnlinePlayers()) {
                Score score = objective.getScore(p.getName());
                score.setScore(main.getPlayersDatabase().getPlayerScore(p));

                p.setScoreboard(board);

                ChatUtils.sendServerMessage(p, ChatUtils.formatMessage("&d", "Tasks game started!"));
                p.playSound(p, Sound.BLOCK_ENCHANTMENT_TABLE_USE, 1.0f, 1.0f);
            }
        }
    }

    private void reset(Player player) throws SQLException {
        // scoreboard reset
        Scoreboard board = Bukkit.getScoreboardManager().getMainScoreboard();

        for (Objective objective : board.getObjectives()) {
            objective.unregister();
        }

        Set<String> entries = board.getEntries();

        for (String entry : entries) {
            board.resetScores(entry);
        }

        // db reset
        for (Player p : Bukkit.getOnlinePlayers()) {
            main.getPlayersDatabase().resetPlayerTasks(p.getUniqueId().toString());
        }

        // players feedback
        player.getServer().broadcastMessage(
                ChatUtils.serverMessage(ChatUtils.formatMessage("&d", "Tasks game reset.")));
    }
}
