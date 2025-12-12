package com.leveling.listeners;

import com.leveling.LevelingPlugin;
import com.leveling.managers.ConfigManager;
import com.leveling.managers.ExperienceManager;
import com.leveling.models.SkillType;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Random;

public class ExcavationListener implements Listener {
    private final LevelingPlugin plugin;
    private final ExperienceManager experienceManager;
    private final Random random = new Random();
    
    public ExcavationListener(LevelingPlugin plugin, ExperienceManager experienceManager) {
        this.plugin = plugin;
        this.experienceManager = experienceManager;
    }
    
    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();
        Material material = block.getType();
        
        // Check if it's an excavation material (dirt, grass, sand, gravel, etc.)
        if (isExcavationMaterial(material)) {
            int exp = plugin.getConfigManager().getExcavationExp(material);
            // Default exp for dirt if not configured
            if (exp == 0 && material == Material.DIRT) {
                exp = 3; // Default dirt exp
            }
            if (exp > 0) {
                experienceManager.addExperience(player, SkillType.EXCAVATION, exp);
                plugin.getHUDManager().showSkillProgress(player, SkillType.EXCAVATION);
                
                // Handle double drops and special drops
                handleExcavationDrops(event, player);
            }
        }
    }
    
    private boolean isExcavationMaterial(Material material) {
        return material == Material.DIRT || 
               material == Material.GRASS_BLOCK ||
               material == Material.GRAVEL ||
               material == Material.SAND ||
               material == Material.RED_SAND ||
               material == Material.CLAY ||
               material == Material.SOUL_SAND ||
               material == Material.SOUL_SOIL ||
               material == Material.MYCELIUM ||
               material == Material.PODZOL ||
               material == Material.COARSE_DIRT ||
               material == Material.ROOTED_DIRT;
    }
    
    private void handleExcavationDrops(BlockBreakEvent event, Player player) {
        int level = plugin.getSkillManager().getLevel(player, SkillType.EXCAVATION);
        
        // Double drops per level (configurable)
        double doubleDropChance = level * plugin.getConfigManager().getExcavationDoubleDropChancePerLevel();
        if (random.nextDouble() < doubleDropChance) {
            // Give double drops
            ItemStack original = new ItemStack(event.getBlock().getType());
            event.getBlock().getWorld().dropItemNaturally(
                event.getBlock().getLocation(), original
            );
        }
        
        // Special drops based on level
        if (level >= 10) {
            double diamondChance = getDiamondChance(level);
            if (random.nextDouble() < diamondChance) {
                event.getBlock().getWorld().dropItemNaturally(
                    event.getBlock().getLocation(), new ItemStack(Material.DIAMOND)
                );
            }
        }
        
        if (level >= 40) {
            // Gold ingot drop at level 40+ (configurable)
            double goldChance = plugin.getConfigManager().getExcavationGoldChance40();
            if (random.nextDouble() < goldChance) {
                event.getBlock().getWorld().dropItemNaturally(
                    event.getBlock().getLocation(), new ItemStack(Material.GOLD_INGOT)
                );
            }
        }
        
        if (level >= 50) {
            // Netherite scrap at max level (configurable)
            double netheriteChance = plugin.getConfigManager().getExcavationNetheriteChance50();
            if (random.nextDouble() < netheriteChance) {
                event.getBlock().getWorld().dropItemNaturally(
                    event.getBlock().getLocation(), new ItemStack(Material.NETHERITE_SCRAP)
                );
            }
        }
    }
    
    private double getDiamondChance(int level) {
        ConfigManager config = plugin.getConfigManager();
        if (level >= 50) return config.getExcavationDiamondChance50();
        if (level >= 40) return config.getExcavationDiamondChance40();
        if (level >= 30) return config.getExcavationDiamondChance30();
        if (level >= 20) return config.getExcavationDiamondChance20();
        if (level >= 10) return config.getExcavationDiamondChance10();
        return 0.0;
    }
}

