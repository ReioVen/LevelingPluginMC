package com.leveling.listeners;

import com.leveling.LevelingPlugin;
import com.leveling.managers.ExperienceManager;
import com.leveling.models.SkillType;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;

import java.util.Random;

public class SmithingListener implements Listener {
    private final LevelingPlugin plugin;
    private final ExperienceManager experienceManager;
    private final Random random = new Random();
    
    public SmithingListener(LevelingPlugin plugin, ExperienceManager experienceManager) {
        this.plugin = plugin;
        this.experienceManager = experienceManager;
    }
    
    @EventHandler
    public void onCraftItem(CraftItemEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) {
            return;
        }
        
        Player player = (Player) event.getWhoClicked();
        double exp = plugin.getConfigManager().getSmithingExpCraft();
        
        experienceManager.addExperience(player, SkillType.SMITHING, exp);
        plugin.getHUDManager().showSkillProgress(player, SkillType.SMITHING);
        
        // Chance to return a resource from the recipe (configurable)
        double returnChance = plugin.getConfigManager().getSmithingResourceReturnChance();
        if (random.nextDouble() < returnChance) {
            Recipe recipe = event.getRecipe();
            if (recipe != null && event.getInventory() instanceof CraftingInventory) {
                CraftingInventory crafting = (CraftingInventory) event.getInventory();
                ItemStack[] matrix = crafting.getMatrix();
                
                // Find a non-air item in the recipe matrix to return
                for (ItemStack item : matrix) {
                    if (item != null && item.getType() != Material.AIR) {
                        ItemStack toReturn = item.clone();
                        toReturn.setAmount(1);
                        player.getInventory().addItem(toReturn);
                        player.sendMessage("§aYou got a resource back from crafting!");
                        break;
                    }
                }
            }
        }
    }
}

