package com.leveling.listeners;

import com.leveling.LevelingPlugin;
import com.leveling.managers.ExperienceManager;
import com.leveling.models.SkillType;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.inventory.ItemStack;

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
        
        // Chance to get double crafted item (0.1% per level, max 5% at level 50)
        int level = plugin.getSkillManager().getLevel(player, SkillType.SMITHING);
        if (level > 0) {
            double doubleCraftChance = level * plugin.getConfigManager().getSmithingResourceReturnChance();
            doubleCraftChance = Math.min(0.05, doubleCraftChance); // Cap at 5% (0.05)
            
            if (random.nextDouble() < doubleCraftChance) {
                // Get the result of the crafting recipe
                ItemStack result = event.getRecipe() != null ? event.getRecipe().getResult() : null;
                if (result == null || result.getType() == Material.AIR) {
                    result = event.getCurrentItem();
                }
                
                if (result != null && result.getType() != Material.AIR) {
                    // Give an extra copy of the crafted item (so you get 2 total: 1 normal + 1 extra)
                    ItemStack extraItem = result.clone();
                    extraItem.setAmount(1); // Give 1 extra item
                    
                    // Add to inventory or drop if full
                    java.util.HashMap<Integer, ItemStack> leftover = player.getInventory().addItem(extraItem);
                    if (!leftover.isEmpty()) {
                        // Drop on ground if inventory is full
                        for (ItemStack item : leftover.values()) {
                            player.getWorld().dropItemNaturally(player.getLocation(), item);
                        }
                    }
                    
                    String itemName = result.getType().name().toLowerCase().replace("_", " ");
                    player.sendMessage("§a§lLucky Craft! §7You got an extra " + itemName + "!");
                }
            }
        }
    }
}

