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
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Random;

public class HerbalismListener implements Listener {
    private final LevelingPlugin plugin;
    private final ExperienceManager experienceManager;
    private final Random random = new Random();
    
    public HerbalismListener(LevelingPlugin plugin, ExperienceManager experienceManager) {
        this.plugin = plugin;
        this.experienceManager = experienceManager;
    }
    
    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();
        Material material = block.getType();
        
        // Check if it's a herbalism material (plants, crops, etc.)
        int exp = plugin.getConfigManager().getHerbalismExp(material);
        if (exp > 0) {
            experienceManager.addExperience(player, SkillType.HERBALISM, exp);
            plugin.getHUDManager().showSkillProgress(player, SkillType.HERBALISM);
            
            int level = plugin.getSkillManager().getLevel(player, SkillType.HERBALISM);
            
            // Double drops per level (configurable)
            double doubleDropChance = level * plugin.getConfigManager().getHerbalismDoubleDropChancePerLevel();
            if (random.nextDouble() < doubleDropChance) {
                ItemStack original = new ItemStack(material);
                event.getBlock().getWorld().dropItemNaturally(
                    event.getBlock().getLocation(), original
                );
            }
            
            // Auto replant at configured level
            int autoReplantLevel = plugin.getConfigManager().getHerbalismAutoReplantLevel();
            if (level >= autoReplantLevel && isCrop(material)) {
                Material seedType = getSeedType(material);
                if (seedType != null && player.getInventory().containsAtLeast(new ItemStack(seedType), 1)) {
                    // Replant after a short delay
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            if (event.getBlock().getType() == Material.AIR) {
                                event.getBlock().setType(material);
                                // Remove one seed from inventory
                                player.getInventory().removeItem(new ItemStack(seedType, 1));
                            }
                        }
                    }.runTaskLater(plugin, 1L);
                }
            }
        }
    }
    
    private boolean isCrop(Material material) {
        return material == Material.WHEAT ||
               material == Material.CARROTS ||
               material == Material.POTATOES ||
               material == Material.BEETROOTS ||
               material == Material.NETHER_WART;
    }
    
    private Material getSeedType(Material crop) {
        switch (crop) {
            case WHEAT: return Material.WHEAT_SEEDS;
            case CARROTS: return Material.CARROT;
            case POTATOES: return Material.POTATO;
            case BEETROOTS: return Material.BEETROOT_SEEDS;
            case NETHER_WART: return Material.NETHER_WART;
            default: return null;
        }
    }
}

