package net.modekh.tweasks.events;

import net.modekh.tweasks.events.base.EventListener;
import net.modekh.tweasks.utils.Task;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;

import java.sql.SQLException;

public class PickupListener implements Listener {
    private final EventListener listener;

    public PickupListener(EventListener listener) {
        this.listener = listener;
    }

    @EventHandler
    public void onPlayerPickup(PlayerPickupItemEvent event) throws SQLException {
        Player player = event.getPlayer();
        ItemStack item = event.getItem().getItemStack();
        ItemStack itemRequired_0 = new ItemStack(Material.AZALEA_LEAVES);
        ItemStack itemRequired_1 = new ItemStack(Material.FLOWERING_AZALEA_LEAVES);

        if (item.equals(itemRequired_0) || item.equals(itemRequired_1)) {
            listener.addScore(player, Task.AZALEA_LEAVES_PICKUP);
        }
    }
}
