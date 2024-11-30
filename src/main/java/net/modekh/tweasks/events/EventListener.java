package net.modekh.tweasks.events;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import net.modekh.tweasks.Tweasks;
import net.modekh.tweasks.commands.TweasksCommand;
import net.modekh.tweasks.utils.messages.ChatUtils;
import net.modekh.tweasks.utils.Task;
import net.modekh.tweasks.utils.messages.event.DeathMessage;
import net.modekh.tweasks.utils.messages.event.RewardMessage;
import net.modekh.tweasks.utils.messages.event.base.EventMessage;
import org.bukkit.*;
import org.bukkit.advancement.Advancement;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.vehicle.VehicleExitEvent;
import org.bukkit.inventory.HorseInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Objective;

import java.sql.SQLException;
import java.util.*;

public class EventListener implements Listener {
    // data
    private final Map<UUID, BukkitRunnable> activeTimers = new HashMap<>();
    private static EventMessage message = null;

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
    public void onPlayerEnterBoat(PlayerInteractEntityEvent event) throws SQLException {
        if (!(event.getRightClicked() instanceof Boat boat
                && boat.getBoatType().equals(Boat.Type.BAMBOO) && containsMobPassenger(boat)))
            return;

        Player player = event.getPlayer();
        UUID playerId = player.getUniqueId();

        if (main.getDatabase().getPlayerTimeSailed(player) > 50) {
            addScore(player, Task.BAMBOO_RAFT_WITH_MOBS);
            return;
        }

        if (activeTimers.containsKey(playerId)) {
            return;
        }

        BukkitRunnable timer = new BukkitRunnable() {
            private int time = 0;

            @Override
            public void run() {
                if (!player.isInsideVehicle() || !(player.getVehicle() instanceof Boat)) {
                    cancel();
                    activeTimers.remove(playerId);
                    return;
                }

                time++;
                main.getDatabase().setPlayerTimeSailed(player, time);
                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, playerId, new TextComponent("Time on the boat: "
                        + ChatUtils.aquaMessage(String.valueOf(time))));
            }
        };

        timer.runTaskTimer(main, 0, 20);
        activeTimers.put(playerId, timer);
    }

    @EventHandler
    public void onPlayerExitBoat(VehicleExitEvent event) throws SQLException {
        if (!(event.getVehicle() instanceof Boat) || !(event.getExited() instanceof Player player))
            return;

        if (main.getDatabase().getPlayerTimeSailed(player) > 50) {
            addScore(player, Task.BAMBOO_RAFT_WITH_MOBS);
            return;
        }

        UUID playerId = player.getUniqueId();

        if (activeTimers.containsKey(playerId)) {
            activeTimers.get(playerId).cancel();
            activeTimers.remove(playerId);
        }

        main.getDatabase().setPlayerTimeSailed(player, 0);
    }

    @EventHandler
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) throws SQLException {
        if (event.getRightClicked() instanceof Cat cat) {
            Player player = event.getPlayer();
            Player owner = (Player) cat.getOwner();

            if (owner == null)
                return;

            String ownerId = owner.getUniqueId().toString();
            String playerId = player.getUniqueId().toString();

            if (ownerId.equals(playerId)) {
                if (player.getItemInHand().getType().equals(Material.NAME_TAG)) {
                    addScore(player, Task.CAT_TAME_NAME);
                }
            }
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) throws SQLException {
        Player player = event.getEntity();

        if (main.getDatabase().addPlayerDeath(player)) {
            addScore(player, Task.DEATHS_LIMIT, false);
        }
    }

    @EventHandler
    public void onChangeDimension(PlayerAdvancementDoneEvent event) throws SQLException {
        Player player = event.getPlayer();
        Advancement theEnd = Bukkit.getAdvancement(NamespacedKey.minecraft("end/root"));

        if (theEnd == null)
            return;

        if (player.getAdvancementProgress(theEnd).isDone()) {
            addScore(player, Task.END);
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

    private void addScore(Player player, Task task, boolean feedback) throws SQLException {
        if (!TweasksCommand.getActivePlayers().contains(player.getUniqueId()))
            return;

        int reward = task.getReward();
        Objective objective = player.getScoreboard().getObjective("tasks");

        if (objective == null)
            return;

        if (this.main.getDatabase().addCompletedTask(player, task)) {
            // scoreboard
            int currentScore = objective.getScore(player.getName()).getScore();
            int newScore = currentScore + reward;

            objective.getScore(player.getDisplayName()).setScore(newScore);

            if (feedback)
                sendTaskFeedback(player, task);
        }
    }

    private void addScore(Player player, Task task) throws SQLException {
        addScore(player, task, true);
    }

    public static void sendTaskFeedback(Player player, Task task) {
        int reward = task.getReward();
        boolean positive = reward >= 0;

        // chat reward message
        if (positive) {
            message = RewardMessage.next((RewardMessage) message);

            if (message != null)
                ChatUtils.sendServerMessage(player, message.get());

            player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0F);
        } else {
            message = DeathMessage.next((DeathMessage) message);

            if (message != null)
                ChatUtils.sendServerMessage(player, message.get());
        }

        player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_BREAK, 1.0f, 1.0F);
    }
}
