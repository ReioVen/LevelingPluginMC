package com.leveling.gui;

import com.leveling.LevelingPlugin;
import com.leveling.managers.ExperienceManager;
import com.leveling.managers.SkillManager;
import com.leveling.models.SkillType;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class SkillDetailGUI {
    private final LevelingPlugin plugin;
    private final SkillManager skillManager;
    private final ExperienceManager experienceManager;
    
    public SkillDetailGUI(LevelingPlugin plugin, SkillManager skillManager, ExperienceManager experienceManager) {
        this.plugin = plugin;
        this.skillManager = skillManager;
        this.experienceManager = experienceManager;
    }
    
    public void openGUI(Player player, SkillType skill) {
        Inventory gui = plugin.getServer().createInventory(null, 54, "§6§l" + skill.getIcon() + " " + skill.getDisplayName());
        
        int level = skillManager.getLevel(player, skill);
        double progress = experienceManager.getProgressPercentage(player, skill);
        double totalExp = experienceManager.getTotalExperience(player, skill);
        double expForNext = experienceManager.getExperienceForNextLevel(player, skill);
        double expNeeded = experienceManager.getExperienceNeededForNextLevel(player, skill);
        
        // Skill icon in center
        ItemStack skillItem = createSkillDisplayItem(skill, level, progress, totalExp, expForNext, expNeeded);
        gui.setItem(22, skillItem);
        
        // Progress bar visualization (using experience bottles)
        createProgressBar(gui, progress, 28, 29, 30, 31, 32, 33, 34);
        
        // Bonuses display
        ItemStack bonusesItem = createBonusesItem(skill, level);
        gui.setItem(40, bonusesItem);
        
        // Leaderboard button
        ItemStack leaderboardItem = GUIManager.createItem(
            Material.PLAYER_HEAD,
            "§e§lView Leaderboard",
            "§7Click to view the",
            "§7leaderboard for this skill"
        );
        gui.setItem(49, leaderboardItem);
        
        // Back button
        ItemStack backItem = GUIManager.createItem(
            Material.ARROW,
            "§7§l← Back to All Skills",
            "§7Click to return to",
            "§7the skills overview"
        );
        gui.setItem(45, backItem);
        
        // Fill empty slots
        GUIManager.fillEmptySlots(gui);
        
        player.openInventory(gui);
    }
    
    private ItemStack createSkillDisplayItem(SkillType skill, int level, double progress, 
                                             double totalExp, double expForNext, double expNeeded) {
        Material material = getSkillMaterial(skill);
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        
        if (meta != null) {
            meta.setDisplayName("§6§l" + skill.getIcon() + " " + skill.getDisplayName());
            
            List<String> lore = new ArrayList<>();
            lore.add("§7");
            lore.add("§7Current Level: §e" + level);
            lore.add("§7");
            lore.add("§7Total Experience: §b" + String.format("%.1f", totalExp));
            lore.add("§7Experience for Next Level: §b" + String.format("%.1f", expForNext));
            lore.add("§7Experience Needed: §b" + String.format("%.1f", expNeeded));
            lore.add("§7");
            lore.add("§7Progress: §b" + String.format("%.1f", progress) + "%");
            
            meta.setLore(lore);
            item.setItemMeta(meta);
        }
        
        return item;
    }
    
    private void createProgressBar(Inventory gui, double progress, int... slots) {
        int filledSlots = (int) Math.round((progress / 100.0) * slots.length);
        
        for (int i = 0; i < slots.length; i++) {
            Material material = i < filledSlots ? Material.EXPERIENCE_BOTTLE : Material.GLASS_BOTTLE;
            String name = i < filledSlots ? "§a█ Progress" : "§8█ Progress";
            
            ItemStack item = new ItemStack(material);
            ItemMeta meta = item.getItemMeta();
            if (meta != null) {
                meta.setDisplayName(name);
                
                // Add lore to show progress information
                List<String> lore = new ArrayList<>();
                lore.add("§7");
                lore.add("§7Progress Bar");
                lore.add("§7");
                if (i < filledSlots) {
                    lore.add("§aFilled §7- Represents progress");
                } else {
                    lore.add("§8Empty §7- Remaining progress");
                }
                lore.add("§7");
                lore.add("§7Current: §b" + String.format("%.1f", progress) + "%");
                
                meta.setLore(lore);
                item.setItemMeta(meta);
            }
            gui.setItem(slots[i], item);
        }
    }
    
    private ItemStack createBonusesItem(SkillType skill, int level) {
        Material material = Material.GOLDEN_APPLE;
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        
        if (meta != null) {
            meta.setDisplayName("§e§lSkill Bonuses");
            
            List<String> lore = new ArrayList<>();
            lore.add("§7");
            
            LevelingPlugin plugin = LevelingPlugin.getPlugin(LevelingPlugin.class);
            
            switch (skill) {
                case MINING:
                    double miningPercent = Math.min(100.0, level * plugin.getConfigManager().getMiningDoubleDropChancePerLevel() * 100);
                    lore.add("§7+§a" + String.format("%.1f", miningPercent) + "% Double Drop Chance");
                    break;
                case EXCAVATION:
                    double excavationPercent = Math.min(100.0, level * plugin.getConfigManager().getExcavationDoubleDropChancePerLevel() * 100);
                    lore.add("§7+§a" + String.format("%.1f", excavationPercent) + "% Double Drop Chance");
                    break;
                case FARMING:
                    double farmingPercent = Math.min(100.0, level * plugin.getConfigManager().getFarmingDoubleDropChancePerLevel() * 100);
                    lore.add("§7+§a" + String.format("%.1f", farmingPercent) + "% Double Drop Chance");
                    int autoReplantLevel = plugin.getConfigManager().getFarmingAutoReplantLevel();
                    if (level >= autoReplantLevel) {
                        lore.add("§7+§aAuto Replant §7(enabled)");
                    } else {
                        lore.add("§7+§aAuto Replant §7(level " + autoReplantLevel + "+)");
                    }
                    break;
                case COMBAT:
                    double combatPercent = Math.min(25.0, level * 0.5);
                    lore.add("§7+§c" + String.format("%.1f", combatPercent) + "% Attack Damage");
                    break;
                case DEFENSE:
                    double defensePercent = Math.min(25.0, level * 0.5);
                    lore.add("§7+§9" + String.format("%.1f", defensePercent) + "% Armor Toughness");
                    break;
                case ACROBATICS:
                    double acrobaticsPercent = Math.min(50.0, level * plugin.getConfigManager().getAcrobaticsDamageReductionPerLevel() * 100);
                    lore.add("§7+§b" + String.format("%.1f", acrobaticsPercent) + "% Fall Damage Reduction");
                    break;
                case SMITHING:
                    double smithingPercent = Math.min(5.0, level * plugin.getConfigManager().getSmithingResourceReturnChance() * 100);
                    lore.add("§7+§e" + String.format("%.2f", smithingPercent) + "% Double Craft Chance");
                    break;
            }
            
            lore.add("§7");
            
            meta.setLore(lore);
            item.setItemMeta(meta);
        }
        
        return item;
    }
    
    private Material getSkillMaterial(SkillType skill) {
        switch (skill) {
            case MINING:
                return Material.DIAMOND_PICKAXE;
            case EXCAVATION:
                return Material.DIAMOND_SHOVEL;
            case WOODCUTTING:
                return Material.DIAMOND_AXE;
            case COMBAT:
                return Material.DIAMOND_SWORD;
            case DEFENSE:
                return Material.SHIELD;
            case FARMING:
                return Material.GOLDEN_HOE;
            case SMITHING:
                return Material.ANVIL;
            case ACROBATICS:
                return Material.FEATHER;
            default:
                return Material.BOOK;
        }
    }
}
