package net.modekh.tweasks.utils.messages;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class ChatUtils {
    public static String formatMessage(String formatChar, String message) {
        return format(formatChar) + message;
    }

    private static String format(String formatChar) {
        return ChatColor.translateAlternateColorCodes('&', formatChar);
    }

    public static String reset(String message) {
        return ChatColor.RESET + message;
    }

    public static void sendInvalidMessage(Player player) {
        ChatUtils.sendServerMessage(player, ChatColor.GOLD + "Invalid command!");
        ChatUtils.sendServerMessage(player, "Command template is: "
                + ChatUtils.formatMessage("&3", "\"/tasks item ")
                + ChatUtils.aquaMessage("<player who guesses your item> <item ID>")
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
}