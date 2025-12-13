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

public class AllSkillsGUI {
    private final LevelingPlugin plugin;
    private final SkillManager skillManager;
    private final ExperienceManager experienceManager;
    
    public AllSkillsGUI(LevelingPlugin plugin, SkillManager skillManager, ExperienceManager experienceManager) {
        this.plugin = plugin;
        this.skillManager = skillManager;
        this.experienceManager = experienceManager;
    }
    
    public void openGUI(Player player) {
        Inventory gui = plugin.getServer().createInventory(null, 54, "§6§lYour Skill Levels");
        
        SkillType[] skills = SkillType.values();
        // Better centered slots for 8 skills in a 54-slot inventory (9x6)
        // Row 2: slots 11, 12, 13, 14 (centered)
        // Row 3: slots 20, 21, 22, 23 (centered)
        int[] skillSlots = {11, 12, 13, 14, 20, 21, 22, 23}; // Better centered slots
        
        for (int i = 0; i < skills.length && i < skillSlots.length; i++) {
            SkillType skill = skills[i];
            int level = skillManager.getLevel(player, skill);
            double progress = experienceManager.getProgressPercentage(player, skill);
            double totalExp = experienceManager.getTotalExperience(player, skill);
            double expForNext = experienceManager.getExperienceForNextLevel(player, skill);
            
            ItemStack skillItem = createSkillItem(skill, level, progress, totalExp, expForNext);
            gui.setItem(skillSlots[i], skillItem);
        }
        
        // Add navigation items
        ItemStack statsItem = GUIManager.createItem(
            Material.BOOK,
            "§e§lView Detailed Statistics",
            "§7Click to view detailed",
            "§7statistics for all skills"
        );
        gui.setItem(49, statsItem);
        
        // Fill empty slots
        GUIManager.fillEmptySlots(gui);
        
        player.openInventory(gui);
    }
    
    private ItemStack createSkillItem(SkillType skill, int level, double progress, double totalExp, double expForNext) {
        Material material = getSkillMaterial(skill);
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        
        if (meta != null) {
            meta.setDisplayName("§6" + skill.getIcon() + " " + skill.getDisplayName());
            
            List<String> lore = new ArrayList<>();
            lore.add("§7");
            lore.add("§7Level: §e" + level);
            lore.add("§7Progress: §b" + String.format("%.1f", progress) + "%");
            lore.add("§7Experience: §b" + String.format("%.0f", totalExp) + "§7/§b" + String.format("%.0f", expForNext));
            lore.add("§7");
            lore.add("§eClick to view details!");
            
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


