package net.modekh.tweasks.events;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import net.modekh.tweasks.Tweasks;
import net.modekh.tweasks.events.base.EventListener;
import net.modekh.tweasks.utils.Task;
import net.modekh.tweasks.utils.messages.ChatUtils;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.vehicle.VehicleExitEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class OnBoatListener implements Listener {
    private final Tweasks main;
    private final EventListener listener;

    private final Map<UUID, BukkitRunnable> activeTimers = new HashMap<>();

    public OnBoatListener(Tweasks main, EventListener listener) {
        this.main = main;
        this.listener = listener;
    }

    @EventHandler
    public void onPlayerEnterBoat(PlayerInteractEntityEvent event) throws SQLException {
        if (!(event.getRightClicked() instanceof Boat boat
                && boat.getBoatType().equals(Boat.Type.BAMBOO) && containsMobPassenger(boat)))
            return;

        Player player = event.getPlayer();
        UUID playerId = player.getUniqueId();

        if (main.getDatabase().getPlayerTimeSailed(player) > 50) {
            listener.addScore(player, Task.BAMBOO_RAFT_WITH_MOBS);
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
            listener.addScore(player, Task.BAMBOO_RAFT_WITH_MOBS);
            return;
        }

        UUID playerId = player.getUniqueId();

        if (activeTimers.containsKey(playerId)) {
            activeTimers.get(playerId).cancel();
            activeTimers.remove(playerId);
        }

        main.getDatabase().setPlayerTimeSailed(player, 0);
    }

    private static boolean containsMobPassenger(Boat boat) {
        for (Entity passenger : boat.getPassengers()) {
            if (passenger instanceof Sheep || passenger instanceof Wolf) {
                return true;
            }
        }

        return false;
    }
}
