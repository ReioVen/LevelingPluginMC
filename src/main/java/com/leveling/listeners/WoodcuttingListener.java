package com.leveling.listeners;

import com.leveling.LevelingPlugin;
import com.leveling.managers.ExperienceManager;
import com.leveling.models.SkillType;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class WoodcuttingListener implements Listener {
    private final LevelingPlugin plugin;
    private final ExperienceManager experienceManager;
    
    public WoodcuttingListener(LevelingPlugin plugin, ExperienceManager experienceManager) {
        this.plugin = plugin;
        this.experienceManager = experienceManager;
    }
    
    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();
        Material material = block.getType();
        
        // Check if it's a log or stem (woodcutting material)
        if (material.name().endsWith("_LOG") || material.name().endsWith("_STEM") || 
            material == Material.MANGROVE_ROOTS || material == Material.CHERRY_LOG) {
            int exp = plugin.getConfigManager().getWoodcuttingExp(material);
            if (exp > 0) {
                experienceManager.addExperience(player, SkillType.WOODCUTTING, exp);
                plugin.getHUDManager().showSkillProgress(player, SkillType.WOODCUTTING);
            }
        }
    }
}

