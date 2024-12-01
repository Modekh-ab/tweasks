package net.modekh.tweasks.events;

import net.modekh.tweasks.events.base.EventListener;
import net.modekh.tweasks.utils.Task;
import org.bukkit.Material;
import org.bukkit.entity.Cat;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;

import java.sql.SQLException;

public class CatInteractListener implements Listener {
    private final EventListener listener;

    public CatInteractListener(EventListener listener) {
        this.listener = listener;
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
                    listener.addScore(player, Task.CAT_TAME_NAME);
                }
            }
        }
    }
}
