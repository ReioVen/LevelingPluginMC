package com.leveling.listeners;

import com.leveling.LevelingPlugin;
import com.leveling.managers.ExperienceManager;
import com.leveling.models.SkillType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityBreedEvent;

public class FarmingListener implements Listener {
    private final LevelingPlugin plugin;
    private final ExperienceManager experienceManager;
    
    public FarmingListener(LevelingPlugin plugin, ExperienceManager experienceManager) {
        this.plugin = plugin;
        this.experienceManager = experienceManager;
    }
    
    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        // Check if it's a crop (already handled by herbalism, but we can add farming exp too)
        // This is for harvesting crops specifically
        if (event.getBlock().getType().name().contains("CROP") || 
            event.getBlock().getType() == org.bukkit.Material.POTATOES ||
            event.getBlock().getType() == org.bukkit.Material.CARROTS ||
            event.getBlock().getType() == org.bukkit.Material.BEETROOTS ||
            event.getBlock().getType() == org.bukkit.Material.WHEAT) {
            double exp = plugin.getConfigManager().getFarmingExpHarvest();
            experienceManager.addExperience(player, SkillType.FARMING, exp);
            plugin.getHUDManager().showSkillProgress(player, SkillType.FARMING);
        }
    }
    
    @EventHandler
    public void onEntityBreed(EntityBreedEvent event) {
        if (event.getBreeder() instanceof Player) {
            Player player = (Player) event.getBreeder();
            double exp = plugin.getConfigManager().getFarmingExpBreed();
            experienceManager.addExperience(player, SkillType.FARMING, exp);
            plugin.getHUDManager().showSkillProgress(player, SkillType.FARMING);
        }
    }
}

