package net.modekh.tweasks.commands;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class TweasksCompleter implements TabCompleter {
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        List<String> commands = new ArrayList<>();
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            commands.add("start");
            commands.add("reset");
            commands.add("item");
            commands.add("guess");

            StringUtil.copyPartialMatches(args[0], commands, completions);
        } else if (args.length == 2) {
            if (args[0].equalsIgnoreCase("item")) {
                for (Material item : Material.values()) {
                    commands.add(item.name().toLowerCase());
                }
            }

            if (args[0].equalsIgnoreCase("guess")) {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    commands.add(player.getName());
                }
            }

            StringUtil.copyPartialMatches(args[1], commands, completions);
        } else if (args.length == 3) {
            for (Material item : Material.values()) {
                commands.add(item.name().toLowerCase());
            }

            StringUtil.copyPartialMatches(args[2], commands, completions);
        }

//        Collections.sort(completions);
        return completions;
    }
}
