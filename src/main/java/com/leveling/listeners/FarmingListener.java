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
import org.bukkit.event.entity.EntityBreedEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Random;

public class FarmingListener implements Listener {
    private final LevelingPlugin plugin;
    private final ExperienceManager experienceManager;
    private final Random random = new Random();
    
    public FarmingListener(LevelingPlugin plugin, ExperienceManager experienceManager) {
        this.plugin = plugin;
        this.experienceManager = experienceManager;
    }
    
    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();
        Material material = block.getType();
        
        // Check if it's a farmable material (crops, plants, etc.)
        int exp = plugin.getConfigManager().getFarmingExp(material);
        if (exp > 0) {
            experienceManager.addExperience(player, SkillType.FARMING, exp);
            plugin.getHUDManager().showSkillProgress(player, SkillType.FARMING);
            
            int level = plugin.getSkillManager().getLevel(player, SkillType.FARMING);
            
            // Double drops per level (2% per level, configurable)
            double doubleDropChance = level * plugin.getConfigManager().getFarmingDoubleDropChancePerLevel();
            if (random.nextDouble() < doubleDropChance) {
                // Get the actual drops that will be given (respects silk touch, etc.)
                ItemStack tool = player.getInventory().getItemInMainHand();
                java.util.Collection<ItemStack> drops = block.getDrops(tool, player);
                
                for (ItemStack drop : drops) {
                    if (drop != null && drop.getType() != Material.AIR) {
                        // Drop a copy of what the player actually receives
                        ItemStack extraDrop = drop.clone();
                        block.getWorld().dropItemNaturally(
                            block.getLocation(), extraDrop
                        );
                    }
                }
            }
            
            // Auto replant at level 30+
            int autoReplantLevel = plugin.getConfigManager().getFarmingAutoReplantLevel();
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
    
    @EventHandler
    public void onEntityBreed(EntityBreedEvent event) {
        if (event.getBreeder() instanceof Player) {
            Player player = (Player) event.getBreeder();
            double exp = plugin.getConfigManager().getFarmingExpBreed();
            experienceManager.addExperience(player, SkillType.FARMING, exp);
            plugin.getHUDManager().showSkillProgress(player, SkillType.FARMING);
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

