package net.modekh.tweasks.events;

import net.modekh.tweasks.events.base.EventListener;
import net.modekh.tweasks.utils.Task;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.HorseInventory;

import java.sql.SQLException;

public class HorseEquipListener implements Listener {
    private final EventListener listener;

    public HorseEquipListener(EventListener listener) {
        this.listener = listener;
    }

    @EventHandler
    public void onPlayerHorseEquip(InventoryClickEvent event) throws SQLException {
        if (event.getWhoClicked() instanceof Player player
                && event.getInventory() instanceof HorseInventory inventory) {
            if (inventory.contains(Material.SADDLE)) {
                if (inventory.contains(Material.IRON_HORSE_ARMOR)
                        || inventory.contains(Material.GOLDEN_HORSE_ARMOR)) {
                    listener.addScore(player, Task.HORSE_EQUIP_ADVANCED);
                } else if (inventory.contains(Material.DIAMOND_HORSE_ARMOR)) {
                    listener.addScore(player, Task.HORSE_EQUIP_DIAMOND);
                } else {
                    listener.addScore(player, Task.HORSE_EQUIP);
                }
            }
        }
    }
}
