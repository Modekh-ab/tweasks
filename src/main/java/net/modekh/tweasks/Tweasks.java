package net.modekh.tweasks;

import net.modekh.tweasks.commands.TweasksCommand;
import net.modekh.tweasks.commands.TweasksCompleter;
import net.modekh.tweasks.db.PlayersDatabase;
import net.modekh.tweasks.events.EventListener;
import net.modekh.tweasks.events.JoinListener;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.SQLException;

public final class Tweasks extends JavaPlugin {
    private PlayersDatabase playersDatabase;

    @Override
    public void onEnable() {
        try {
            if (!getDataFolder().exists()) {
                getDataFolder().mkdirs();
            }

            playersDatabase = new PlayersDatabase(getDataFolder().getAbsolutePath() + "/tweasks.db");
        } catch (SQLException e) {
            e.printStackTrace();
            Bukkit.getPluginManager().disablePlugin(this);
        }

        getServer().getPluginCommand("tasks").setExecutor(new TweasksCommand(this));
        getCommand("tasks").setTabCompleter(new TweasksCompleter());

        getServer().getPluginManager().registerEvents(new JoinListener(this), this);
        getServer().getPluginManager().registerEvents(new EventListener(this), this);
    }

    @Override
    public void onDisable() {
        try {
            playersDatabase.closeConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public PlayersDatabase getPlayersDatabase() {
        return playersDatabase;
    }

    // chiseled bookshelf new recipe
    static {
        ShapedRecipe recipe = new ShapedRecipe(new ItemStack(Material.CHISELED_BOOKSHELF));
        recipe.shape(
                "TST",
                "T#T",
                "TST");

        recipe.setIngredient('#', Material.BOOKSHELF);
        recipe.setIngredient('S', Material.OAK_SLAB);
        recipe.setIngredient('T', Material.OAK_TRAPDOOR);

        Bukkit.addRecipe(recipe);
    }
}
