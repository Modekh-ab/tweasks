package net.modekh.tweasks.events;

import net.modekh.tweasks.utils.ChatUtils;
import net.modekh.tweasks.utils.Task;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scoreboard.Objective;

import java.util.HashMap;
import java.util.UUID;

public class EventListener implements Listener {
    private static HashMap<UUID, Integer> playerTaskData = new HashMap<>();

    @EventHandler
    public void onPlayerCraft(CraftItemEvent event) {
        Material itemRequired = Material.CHISELED_BOOKSHELF;
        Material result = event.getInventory().getResult().getType();
        Player player = (Player) event.getWhoClicked();

        if (result.equals(itemRequired)) {
            addScore(player, 1, Task.CHISELED_BOOKSHELF_CRAFT);
        }
    }

    @EventHandler
    public void onPlayerPickup(PlayerPickupItemEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem().getItemStack();
        ItemStack itemRequired_0 = new ItemStack(Material.AZALEA_LEAVES);
        ItemStack itemRequired_1 = new ItemStack(Material.FLOWERING_AZALEA_LEAVES);

        if (item.equals(itemRequired_0) || item.equals(itemRequired_1)) {
            addScore(player, 1, Task.AZALEA_LEAVES_PICKUP);
        }
    }

    private static void addScore(Player player, int reward, Task task) {
        if (!playerTaskData.containsValue(task.ordinal())) {
            playerTaskData.put(player.getUniqueId(), task.ordinal());

            Objective objective = player.getScoreboard().getObjective("tasks");

            if (objective != null) {
                int currentScore = objective.getScore(player.getName()).getScore();
                objective.getScore(player.getName()).setScore(currentScore + reward);
            }

            ChatUtils.sendServerMessage(player, "Yeah, take your point!");
            player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0F);
        }
    }
}
