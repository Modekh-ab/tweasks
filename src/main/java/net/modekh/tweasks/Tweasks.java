package net.modekh.tweasks;

import net.modekh.tweasks.commands.TweasksCommand;
import net.modekh.tweasks.commands.TweasksCompleter;
import net.modekh.tweasks.events.EventListener;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.plugin.java.JavaPlugin;

public final class Tweasks extends JavaPlugin {
    @Override
    public void onEnable() {
        getServer().getPluginCommand("tasks").setExecutor(new TweasksCommand());
        getCommand("tasks").setTabCompleter(new TweasksCompleter());

        getServer().getPluginManager().registerEvents(new EventListener(), this);
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
