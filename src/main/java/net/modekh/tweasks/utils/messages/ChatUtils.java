package net.modekh.tweasks.utils.messages;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class ChatUtils {
    public static void sendInvalidMessageItem(Player player) {
        ChatUtils.sendServerMessage(player, ChatColor.GOLD + "Invalid command!");
        ChatUtils.sendServerMessage(player, "Usage: "
                + ChatUtils.formatMessage("&3", "\"/tasks item ") + ChatUtils.aquaMessage("<item_id>")
                + ChatUtils.formatMessage("&3", "\""));
    }

    public static void sendInvalidMessageGuess(Player player) {
        ChatUtils.sendServerMessage(player, ChatColor.GOLD + "Invalid command!");
        ChatUtils.sendServerMessage(player, "Usage: "
                + ChatUtils.formatMessage("&3", "\"/tasks guess ")
                + ChatUtils.aquaMessage("<opponent_username> <item_id>")
                + ChatUtils.formatMessage("&3", "\""));
    }

    public static void sendServerMessage(Player player, String message) {
        player.sendMessage(messageSender() + message);
    }

    public static String serverMessage(String message) {
        return messageSender() + message;
    }

    public static String aquaMessage(String message) {
        return ChatColor.AQUA + message;
    }

    private static String messageSender() {
        return ChatColor.DARK_GREEN + "[" + ChatColor.GREEN + "Server Tasks" + ChatColor.DARK_GREEN + "] " + ChatColor.RESET;
    }

    public static String formatMessage(String formatChar, String message) {
        return format(formatChar) + message;
    }

    private static String format(String formatChar) {
        return ChatColor.translateAlternateColorCodes('&', formatChar);
    }

    public static String reset(String message) {
        return ChatColor.RESET + message;
    }
}
