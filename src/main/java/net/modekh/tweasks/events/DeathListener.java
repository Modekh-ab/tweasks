package net.modekh.tweasks.events;

import net.modekh.tweasks.Tweasks;
import net.modekh.tweasks.events.base.EventListener;
import net.modekh.tweasks.utils.Task;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import java.sql.SQLException;

public class DeathListener implements Listener {
    private final Tweasks main;
    private final EventListener listener;

    public DeathListener(Tweasks main, EventListener listener) {
        this.main = main;
        this.listener = listener;
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) throws SQLException {
        Player player = event.getEntity();

        if (main.getDatabase().addPlayerDeath(player)) {
            listener.addScore(player, Task.DEATHS_LIMIT, false);
        }
    }
}
