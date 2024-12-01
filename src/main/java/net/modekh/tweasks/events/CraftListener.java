package net.modekh.tweasks.events;

import net.modekh.tweasks.events.base.EventListener;
import net.modekh.tweasks.utils.Task;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;

import java.sql.SQLException;

public class CraftListener implements Listener {
    private final EventListener listener;

    public CraftListener(EventListener listener) {
        this.listener = listener;
    }

    @EventHandler
    public void onPlayerCraft(CraftItemEvent event) throws SQLException {
        Material itemRequired = Material.CHISELED_BOOKSHELF;
        Material result = event.getInventory().getResult().getType();
        Player player = (Player) event.getWhoClicked();

        if (result.equals(itemRequired)) {
            listener.addScore(player, Task.CHISELED_BOOKSHELF_CRAFT);
        }
    }
}
