package net.modekh.tweasks.events;

import net.modekh.tweasks.Tweasks;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.sql.SQLException;

public class JoinListener implements Listener {
    private final Tweasks main;

    public JoinListener(Tweasks main) {
        this.main = main;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) throws SQLException {
        Player player = event.getPlayer();

        if (!player.hasPlayedBefore()) {
            main.getPlayersDatabase().addPlayer(player);
        }
    }
}
