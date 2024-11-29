package net.modekh.tweasks.commands;

import net.modekh.tweasks.utils.ChatUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scoreboard.*;

public class TweasksCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player player) {
            if (args.length == 0) {
                ChatUtils.sendServerMessage(player, ChatColor.GOLD + "Invalid command!");
                return false;
            }

            if (args[0].equalsIgnoreCase("start")) {
                createScoreboard(player);
                return true;
            }

            if (args[0].equalsIgnoreCase("item")) {
                if (args.length != 3) {
                    sendInvalidMessage(player);
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
                    sendInvalidMessage(player);
                    return false;
                }
            }
        }

        return false;
    }

    private static void sendInvalidMessage(Player player) {
        ChatUtils.sendServerMessage(player, ChatColor.GOLD + "Invalid command!");
        ChatUtils.sendServerMessage(player, "Command template is: "
                + ChatUtils.formatMessage("&3", "\"/tasks item ")
                + ChatUtils.aquaMessage("<player who guesses your item> <item ID>")
                + ChatUtils.formatMessage("&3", "\""));
    }

    private static void createScoreboard(Player player) {
        ScoreboardManager manager = Bukkit.getScoreboardManager();
        Scoreboard board = manager.getNewScoreboard();
        Objective objective = board.registerNewObjective("tasks", "dummy");

        objective.setDisplayName(ChatUtils.formatMessage("&l&a", "Score"));
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);

        for (Player p : Bukkit.getOnlinePlayers()) {
            Score score = objective.getScore(p.getName());
            score.setScore(2); // initial score for all
        }

        player.setScoreboard(board);
    }
}
