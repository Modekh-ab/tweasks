package net.modekh.tweasks.events;

import net.modekh.tweasks.events.base.EventListener;
import net.modekh.tweasks.utils.Task;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.advancement.Advancement;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerAdvancementDoneEvent;

import java.sql.SQLException;

public class ChangeDimensionListener implements Listener {
    private final EventListener listener;

    public ChangeDimensionListener(EventListener listener) {
        this.listener = listener;
    }

    @EventHandler
    public void onChangeDimension(PlayerAdvancementDoneEvent event) throws SQLException {
        Player player = event.getPlayer();
        Advancement theEnd = Bukkit.getAdvancement(NamespacedKey.minecraft("end/root"));

        if (theEnd == null)
            return;

        if (player.getAdvancementProgress(theEnd).isDone()) {
            listener.addScore(player, Task.END);
        }
    }
}
