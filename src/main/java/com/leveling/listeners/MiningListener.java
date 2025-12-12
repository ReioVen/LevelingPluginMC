package com.leveling.listeners;

import com.leveling.LevelingPlugin;
import com.leveling.managers.ExperienceManager;
import com.leveling.models.SkillType;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
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
        if (event.isCancelled()) {
            return;
        }
        
        Player player = event.getPlayer();
        Block block = event.getBlock();
        Material material = block.getType();
        
        // Check if it's a mining material
        int exp = plugin.getConfigManager().getMiningExp(material);
        if (exp > 0) {
            experienceManager.addExperience(player, SkillType.MINING, exp);
            plugin.getHUDManager().showSkillProgress(player, SkillType.MINING);
            
            // Handle double drops (2% per level) + fortune effect
            handleMiningDrops(event, player, block);
        }
    }
    
    private void handleMiningDrops(BlockBreakEvent event, Player player, Block block) {
        int level = plugin.getSkillManager().getLevel(player, SkillType.MINING);
        
        if (level <= 0) {
            return;
        }
        
        Material blockType = block.getType();
        ItemStack tool = player.getInventory().getItemInMainHand();
        boolean hasSilkTouch = tool != null && tool.getEnchantmentLevel(Enchantment.SILK_TOUCH) > 0;
        
        // For ores: don't duplicate if Silk Touch is present (drops ore block, not processed item)
        if (isOre(blockType) && hasSilkTouch) {
            return; // Skip double drops for ores with Silk Touch
        }
        
        // Double drop chance per level (configurable)
        // Works for ALL mining blocks including ores - drops actual items (diamonds, ingots, etc.)
        double doubleDropChance = level * plugin.getConfigManager().getMiningDoubleDropChancePerLevel();
        // Cap at 100% (1.0) and use <= to ensure 100% always triggers
        doubleDropChance = Math.min(1.0, doubleDropChance);
        
        // At level 50 with 2% per level = 100%, should always trigger
        boolean shouldDoubleDrop = doubleDropChance >= 1.0 || random.nextDouble() <= doubleDropChance;
        
        if (shouldDoubleDrop) {
            // Get the actual drops that will be given (respects fortune, silk touch, etc.)
            // For ores without Silk Touch: drops diamonds/ingots
            // For ores with Silk Touch: skipped (returns early above)
            // For stone: drops cobblestone (or stone with silk touch)
            java.util.Collection<ItemStack> drops;
            
            try {
                // Try with tool and player first (respects fortune, silk touch)
                drops = block.getDrops(tool, player);
            } catch (Exception e) {
                // Fallback to basic drops
                drops = block.getDrops();
            }
            
            if (drops.isEmpty()) {
                // If still no drops, try without tool parameter
                drops = block.getDrops();
            }
            
            for (ItemStack drop : drops) {
                if (drop != null && drop.getType() != Material.AIR && drop.getAmount() > 0) {
                    // For ores: only duplicate processed items (iron ingots, diamonds), not ore blocks
                    // Skip if it's an ore block item (Silk Touch drop)
                    if (isOreItem(drop.getType())) {
                        continue; // Don't duplicate ore blocks
                    }
                    
                    // Drop a copy of what the player actually receives
                    // For diamond ore without Silk Touch: drops diamonds (respects fortune)
                    // For iron ore without Silk Touch: drops iron ingots (respects fortune)
                    // For stone: drops cobblestone (or stone with silk touch)
                    ItemStack extraDrop = drop.clone();
                    block.getWorld().dropItemNaturally(
                        block.getLocation(), extraDrop
                    );
                }
            }
        }
    }
    
    private boolean isOre(Material material) {
        String name = material.name().toLowerCase();
        return name.contains("_ore") || material == Material.ANCIENT_DEBRIS;
    }
    
    private boolean isOreItem(Material material) {
        // Check if the dropped item is an ore block (from Silk Touch)
        String name = material.name().toLowerCase();
        return name.contains("_ore") || material == Material.ANCIENT_DEBRIS;
    }
}

