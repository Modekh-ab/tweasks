package net.modekh.tweasks.commands;

import net.modekh.tweasks.Tweasks;
import net.modekh.tweasks.events.base.EventListener;
import net.modekh.tweasks.utils.Task;
import net.modekh.tweasks.utils.messages.ChatUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Objective;

import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class TweasksCommand implements CommandExecutor {
    private static final Set<UUID> activePlayers = new HashSet<>();
    private static final Set<UUID> unsolvedPlayers = new HashSet<>();
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

        // commands without op requirement

        if (args[0].equalsIgnoreCase("item")) {
            if (args.length != 2) {
                ChatUtils.sendInvalidMessageItem(player);
                return false;
            }

            try {
                String itemId = args[1];

                if (main.getDatabase().addItemToGuess(player, itemId)) {
                    unsolvedPlayers.add(player.getUniqueId());
                    ChatUtils.sendServerMessage(player, "Your item now is "
                            + ChatUtils.aquaMessage(itemId) + ChatUtils.reset("."));
                    player.getServer().broadcastMessage(
                            ChatUtils.serverMessage(getAquaName(player) + " had just chosen an item to guess!"));

                    return true;
                }

                ChatUtils.sendServerMessage(player, ChatUtils.formatMessage("&6", "Invalid item!"));

                return false;
            } catch (Exception e) {
                ChatUtils.sendInvalidMessageItem(player);
                return false;
            }
        }

        if (args[0].equalsIgnoreCase("guess")) {
            if (args.length != 3) {
                ChatUtils.sendInvalidMessageGuess(player);
                return false;
            }

            try {
                Player opponent = Bukkit.getPlayer(args[1]);
                String itemId = args[2];

                if (opponent == null || opponent.equals(player))
                    return false;

                if (main.getDatabase().guessItem(opponent, itemId)) {
                    boolean guessed = main.getDatabase().addCompletedTask(player, Task.ITEM_GUESS);

                    if (!guessed) {
                        ChatUtils.sendServerMessage(player, "You already guessed " + getAquaName(opponent) + "'s item.");
                        return false;
                    }

                    addScore(player);

                    unsolvedPlayers.remove(player.getUniqueId());
                    ChatUtils.sendServerMessage(player, "You guessed "
                            + ChatUtils.aquaMessage(itemId) + ChatUtils.reset("!"));
                    ChatUtils.sendServerMessage(opponent, ChatUtils.formatMessage("&6",
                            "Yo, " + getAquaName(player) + " just guessed your item... But shh!"));

                    return true;
                }

                ChatUtils.sendServerMessage(player,ChatUtils.formatMessage("&6", "Nope!"));

                return false;
            } catch (Exception e) {
                ChatUtils.sendInvalidMessageGuess(player);
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
                start();
                return true;
            } catch (SQLException e) {
                e.printStackTrace();
                return false;
            }
        }

        if (args[0].equalsIgnoreCase("reset")) {
            try {
                reset();
                return true;
            } catch (SQLException e) {
                e.printStackTrace();
                return false;
            }
        }

        return false;
    }

    private void start() throws SQLException {
        for (Player player : Bukkit.getOnlinePlayers()) {
            main.getDatabase().addPlayerData(player);
            main.getScoreboard().addPlayerToScoreboard(player);
            activePlayers.add(player.getUniqueId());

            ChatUtils.sendServerMessage(player, ChatUtils.formatMessage("&d", "Tasks game started!"));
            player.playSound(player, Sound.BLOCK_ENCHANTMENT_TABLE_USE, 1.0f, 1.0f);
        }
    }

    private void reset() throws SQLException {
        main.getDatabase().resetDatabase();
        main.getScoreboard().resetScoreboard();

        activePlayers.clear();
        unsolvedPlayers.clear();

        for (Player player : Bukkit.getOnlinePlayers()) {
            // players feedback
            player.playSound(player, Sound.ENCHANT_THORNS_HIT, 1.0f, 1.0f);
            player.getServer().broadcastMessage(
                    ChatUtils.serverMessage(ChatUtils.formatMessage("&d", "Tasks game reset.")));
        }
    }

    private void addScore(Player player) {
        if (!TweasksCommand.getActivePlayers().contains(player.getUniqueId()))
            return;

        int reward = Task.ITEM_GUESS.getReward(); // function only for one task
        Objective objective = player.getScoreboard().getObjective("tasks");

        if (objective == null)
            return;

        // scoreboard
        int currentScore = objective.getScore(player.getName()).getScore();
        int newScore = currentScore + reward;

        objective.getScore(player.getName()).setScore(newScore);

        EventListener.sendTaskFeedback(player, Task.ITEM_GUESS);
    }

    public static Set<UUID> getActivePlayers() {
        return activePlayers;
    }

    private static String getAquaName(Player player) {
        return ChatUtils.aquaMessage(player.getDisplayName()) + ChatColor.RESET;
    }
}
