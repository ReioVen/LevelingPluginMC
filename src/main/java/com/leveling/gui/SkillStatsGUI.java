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

public class SkillStatsGUI {
    private final LevelingPlugin plugin;
    private final SkillManager skillManager;
    private final ExperienceManager experienceManager;
    
    public SkillStatsGUI(LevelingPlugin plugin, SkillManager skillManager, ExperienceManager experienceManager) {
        this.plugin = plugin;
        this.skillManager = skillManager;
        this.experienceManager = experienceManager;
    }
    
    public void openGUI(Player player) {
        Inventory gui = plugin.getServer().createInventory(null, 54, "§6§lDetailed Skill Statistics");
        
        SkillType[] skills = SkillType.values();
        // Better centered slots for 8 skills in a 54-slot inventory (9x6)
        // Row 2: slots 11, 12, 13, 14 (centered)
        // Row 3: slots 20, 21, 22, 23 (centered)
        int[] skillSlots = {11, 12, 13, 14, 20, 21, 22, 23}; // Better centered slots
        
        for (int i = 0; i < skills.length && i < skillSlots.length; i++) {
            SkillType skill = skills[i];
            ItemStack skillItem = createDetailedSkillItem(player, skill);
            gui.setItem(skillSlots[i], skillItem);
        }
        
        // Fill empty slots
        GUIManager.fillEmptySlots(gui);
        
        player.openInventory(gui);
    }
    
    private ItemStack createDetailedSkillItem(Player player, SkillType skill) {
        int level = skillManager.getLevel(player, skill);
        double totalExp = experienceManager.getTotalExperience(player, skill);
        double progress = experienceManager.getProgressPercentage(player, skill);
        double expForNext = experienceManager.getExperienceForNextLevel(player, skill);
        
        Material material = getSkillMaterial(skill);
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        
        if (meta != null) {
            meta.setDisplayName("§6" + skill.getIcon() + " " + skill.getDisplayName());
            
            List<String> lore = new ArrayList<>();
            lore.add("§7");
            lore.add("§7Level: §e" + level);
            lore.add("§7Total Experience: §b" + String.format("%.1f", totalExp));
            lore.add("§7Experience for Next: §b" + String.format("%.1f", expForNext));
            lore.add("§7Progress: §b" + String.format("%.1f", progress) + "%");
            lore.add("§7");
            lore.add("§e§lBonuses:");
            
            // Add bonuses based on skill type
            switch (skill) {
                case MINING:
                    double miningPercent = Math.min(100.0, level * plugin.getConfigManager().getMiningDoubleDropChancePerLevel() * 100);
                    lore.add("  §7+§a" + String.format("%.1f", miningPercent) + "% Double Drop Chance");
                    break;
                case EXCAVATION:
                    double excavationPercent = Math.min(100.0, level * plugin.getConfigManager().getExcavationDoubleDropChancePerLevel() * 100);
                    lore.add("  §7+§a" + String.format("%.1f", excavationPercent) + "% Double Drop Chance");
                    break;
                case FARMING:
                    double farmingPercent = Math.min(100.0, level * plugin.getConfigManager().getFarmingDoubleDropChancePerLevel() * 100);
                    lore.add("  §7+§a" + String.format("%.1f", farmingPercent) + "% Double Drop Chance");
                    int autoReplantLevel = plugin.getConfigManager().getFarmingAutoReplantLevel();
                    if (level >= autoReplantLevel) {
                        lore.add("  §7+§aAuto Replant §7(enabled)");
                    } else {
                        lore.add("  §7+§aAuto Replant §7(level " + autoReplantLevel + "+)");
                    }
                    break;
                case COMBAT:
                    double combatPercent = Math.min(25.0, level * 0.5);
                    lore.add("  §7+§c" + String.format("%.1f", combatPercent) + "% Attack Damage");
                    break;
                case DEFENSE:
                    double defensePercent = Math.min(25.0, level * 0.5);
                    lore.add("  §7+§9" + String.format("%.1f", defensePercent) + "% Armor Toughness");
                    break;
                case ACROBATICS:
                    double acrobaticsPercent = Math.min(50.0, level * plugin.getConfigManager().getAcrobaticsDamageReductionPerLevel() * 100);
                    lore.add("  §7+§b" + String.format("%.1f", acrobaticsPercent) + "% Fall Damage Reduction");
                    break;
                case SMITHING:
                    double smithingPercent = Math.min(5.0, level * plugin.getConfigManager().getSmithingResourceReturnChance() * 100);
                    lore.add("  §7+§e" + String.format("%.2f", smithingPercent) + "% Double Craft Chance");
                    break;
            }
            
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



