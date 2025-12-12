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
import org.bukkit.inventory.ItemStack;

import java.util.Random;

public class MiningListener implements Listener {
    private final LevelingPlugin plugin;
    private final ExperienceManager experienceManager;
    private final Random random = new Random();
    
    public MiningListener(LevelingPlugin plugin, ExperienceManager experienceManager) {
        this.plugin = plugin;
        this.experienceManager = experienceManager;
    }
    
    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();
        Material material = block.getType();
        
        // Check if it's a mining material
        int exp = plugin.getConfigManager().getMiningExp(material);
        if (exp > 0) {
            experienceManager.addExperience(player, SkillType.MINING, exp);
            plugin.getHUDManager().showSkillProgress(player, SkillType.MINING);
            
            // Handle double drops (2% per level) + fortune effect
            handleMiningDrops(event, player);
        }
    }
    
    private void handleMiningDrops(BlockBreakEvent event, Player player) {
        int level = plugin.getSkillManager().getLevel(player, SkillType.MINING);
        
        // Double drop chance per level (configurable)
        double doubleDropChance = level * plugin.getConfigManager().getMiningDoubleDropChancePerLevel();
        if (random.nextDouble() < doubleDropChance) {
            // Give double drops
            ItemStack original = new ItemStack(event.getBlock().getType());
            event.getBlock().getWorld().dropItemNaturally(
                event.getBlock().getLocation(), original
            );
        }
        
        // Fortune effect is handled by Minecraft's natural system
        // The double drops from mining level are in addition to fortune
    }
}

