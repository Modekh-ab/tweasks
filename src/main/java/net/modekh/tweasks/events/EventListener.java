package net.modekh.tweasks.events;

import net.modekh.tweasks.Tweasks;
import net.modekh.tweasks.utils.messages.ChatUtils;
import net.modekh.tweasks.utils.Task;
import net.modekh.tweasks.utils.messages.event.DeathMessage;
import net.modekh.tweasks.utils.messages.event.RewardMessage;
import net.modekh.tweasks.utils.messages.event.base.EventMessage;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.Statistic;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.HorseInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scoreboard.Objective;

import java.sql.Array;
import java.sql.SQLException;
import java.util.*;

public class EventListener implements Listener {
    // data

    private static List<Task> tasks = new ArrayList<>();

    private static HashMap<UUID, Integer> raftSailData = new HashMap<>();
    private static HashMap<UUID, Integer> deathData = new HashMap<>();
    private static EventMessage message = RewardMessage.MSG_0;

    // constructor

    private final Tweasks main;

    public EventListener(Tweasks main) {
        this.main = main;
    }

    // events

    @EventHandler
    public void onPlayerCraft(CraftItemEvent event) throws SQLException {
        Material itemRequired = Material.CHISELED_BOOKSHELF;
        Material result = event.getInventory().getResult().getType();
        Player player = (Player) event.getWhoClicked();

        if (result.equals(itemRequired)) {
            addScore(player, Task.CHISELED_BOOKSHELF_CRAFT);
        }
    }

    @EventHandler
    public void onPlayerPickup(PlayerPickupItemEvent event) throws SQLException {
        Player player = event.getPlayer();
        ItemStack item = event.getItem().getItemStack();
        ItemStack itemRequired_0 = new ItemStack(Material.AZALEA_LEAVES);
        ItemStack itemRequired_1 = new ItemStack(Material.FLOWERING_AZALEA_LEAVES);

        if (item.equals(itemRequired_0) || item.equals(itemRequired_1)) {
            addScore(player, Task.AZALEA_LEAVES_PICKUP);
        }
    }

    @EventHandler
    public void onPlayerInventoryInteract(InventoryClickEvent event) throws SQLException {
        if (event.getWhoClicked() instanceof Player player
                && event.getInventory() instanceof HorseInventory inventory) {
            if (inventory.contains(Material.SADDLE)) {
                if (inventory.contains(Material.IRON_HORSE_ARMOR)
                        || inventory.contains(Material.GOLDEN_HORSE_ARMOR)) {
                    addScore(player, Task.HORSE_EQUIP_ADVANCED);
                } else if (inventory.contains(Material.DIAMOND_HORSE_ARMOR)) {
                    addScore(player, Task.HORSE_EQUIP_DIAMOND);
                } else {
                    addScore(player, Task.HORSE_EQUIP);
                }
            }
        }
    }

    @EventHandler
    public void onPlayerInteractAtEntity(PlayerInteractAtEntityEvent event) {
        if (event.getRightClicked() instanceof Boat boat) {
            if (boat.getBoatType().equals(Boat.Type.BAMBOO)) {
                Player player = event.getPlayer();

                if (containsMobPassenger(boat)) {
                    raftSailData.put(player.getUniqueId(), player.getStatistic(Statistic.BOAT_ONE_CM));
                }
            }
        }
    }

    @EventHandler
    public void onPlayerToggleSneak(PlayerToggleSneakEvent event) throws SQLException {
        Player player = event.getPlayer();
        UUID playerId = player.getUniqueId();
        int sailStat = raftSailData.get(playerId);

        if (sailStat > 0) {
            int currentSailStat = player.getStatistic(Statistic.BOAT_ONE_CM) - sailStat;

            if (currentSailStat > 10) {
                addScore(player, Task.BAMBOO_RAFT_WITH_MOBS);
            }
        }
    }

    @EventHandler
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) throws SQLException {
        if (event.getRightClicked() instanceof Cat cat) {
            Player player = event.getPlayer();

            if (cat.getOwner().getUniqueId().equals(player.getUniqueId())) {
                ItemStack nameTagItem = new ItemStack(Material.NAME_TAG);
                player.sendMessage("doing greattttt");

                if (Objects.equals(player.getItemInUse(), nameTagItem)) {
                    addScore(player, Task.CAT_TAME_NAME);
                }
            }
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        UUID playerId = player.getUniqueId();
        int currentDeaths = deathData.getOrDefault(playerId, 0);

        deathData.put(playerId, currentDeaths + 1);
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) throws SQLException {
        Player player = event.getPlayer();

        if (deathData.get(player.getUniqueId()) > 4) {
            addScore(player, Task.DEATHS_LIMIT);
        }
    }

    // utils

    private static boolean containsMobPassenger(Boat boat) {
        for (Entity passenger : boat.getPassengers()) {
            if (passenger instanceof Sheep || passenger instanceof Wolf) {
                return true;
            }
        }

        return false;
    }

    private void addScore(Player player, Task task) throws SQLException {
        int reward = task.getReward();
        boolean positive = reward >= 0;

        if (!main.getPlayersDatabase().getPlayerTasks(player).contains(task)) {
            tasks.add(task);
            main.getPlayersDatabase().savePlayerTasks(player.getUniqueId().toString(), tasks);

            Objective objective = player.getScoreboard().getObjective("tasks");

            if (objective != null) {
                int currentScore = objective.getScore(player.getName()).getScore();
                int newScore = currentScore + reward;
                objective.getScore(player.getName()).setScore(newScore);

                main.getPlayersDatabase().updatePlayerData(player, newScore, tasks);

                if (positive) {
                    message = RewardMessage.next((RewardMessage) message);

                    if (message != null)
                        ChatUtils.sendServerMessage(player, message.get());

                    player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0F);
                }
            }
        } else {
            if (!positive) {
                message = DeathMessage.next((DeathMessage) message);

                if (message != null)
                    ChatUtils.sendServerMessage(player, message.get());

                player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_BREAK, 1.0f, 1.0F);
            }
        }
    }
}
