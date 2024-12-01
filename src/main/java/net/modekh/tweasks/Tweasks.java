package net.modekh.tweasks;

import net.modekh.tweasks.commands.TweasksCommand;
import net.modekh.tweasks.commands.TweasksCompleter;
import net.modekh.tweasks.events.*;
import net.modekh.tweasks.events.base.EventListener;
import net.modekh.tweasks.handlers.TweasksDatabase;
import net.modekh.tweasks.handlers.TweasksScoreboard;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.SQLException;

public final class Tweasks extends JavaPlugin {
    private TweasksDatabase playersDatabase;
    private TweasksScoreboard playersScoreboard;

    @Override
    public void onEnable() {
        // main classes init
        try {
            if (!getDataFolder().exists()) {
                getDataFolder().mkdirs();
            }

            playersDatabase = new TweasksDatabase(getDataFolder().getAbsolutePath() + "/tweasks.db");
        } catch (SQLException e) {
            e.printStackTrace();
            Bukkit.getPluginManager().disablePlugin(this);
        }

        playersScoreboard = new TweasksScoreboard(this);
        EventListener eventListener = new EventListener(this);

        // commands init
        getServer().getPluginCommand("tasks").setExecutor(new TweasksCommand(this));
        getCommand("tasks").setTabCompleter(new TweasksCompleter());

        // base listener
        getServer().getPluginManager().registerEvents(eventListener, this);
        // tasks listeners
        getServer().getPluginManager().registerEvents(new JoinListener(this), this);
        getServer().getPluginManager().registerEvents(new CraftListener(eventListener), this);
        getServer().getPluginManager().registerEvents(new PickupListener(eventListener), this);
        getServer().getPluginManager().registerEvents(new HorseEquipListener(eventListener), this);
        getServer().getPluginManager().registerEvents(new CatInteractListener(eventListener), this);
        getServer().getPluginManager().registerEvents(new ChangeDimensionListener(eventListener), this);
        getServer().getPluginManager().registerEvents(new OnBoatListener(this, eventListener), this);
        getServer().getPluginManager().registerEvents(new DeathListener(this, eventListener), this);
    }

    @Override
    public void onDisable() {
        try {
            playersDatabase.closeConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public TweasksDatabase getDatabase() {
        return playersDatabase;
    }

    public TweasksScoreboard getScoreboard() {
        return playersScoreboard;
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
        Bukkit.removeRecipe(NamespacedKey.minecraft("chiseled_bookshelf"));
    }
}
