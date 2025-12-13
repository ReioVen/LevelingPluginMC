package com.leveling.commands;

import com.leveling.LevelingPlugin;
import com.leveling.managers.ExperienceManager;
import com.leveling.managers.SkillManager;
import com.leveling.models.SkillType;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SkillStatsCommand implements CommandExecutor {
    private final SkillManager skillManager;
    private final ExperienceManager experienceManager;
    
    public SkillStatsCommand(SkillManager skillManager, ExperienceManager experienceManager) {
        this.skillManager = skillManager;
        this.experienceManager = experienceManager;
    }
    
    private LevelingPlugin getPlugin() {
        return LevelingPlugin.getPlugin(LevelingPlugin.class);
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cThis command can only be used by players!");
            return true;
        }
        
        Player player = (Player) sender;
        
        player.sendMessage("§6§l=== Detailed Level Statistics ===");
        
        for (SkillType skill : SkillType.values()) {
            int level = skillManager.getLevel(player, skill);
            double totalExp = experienceManager.getTotalExperience(player, skill);
            double progress = experienceManager.getProgressPercentage(player, skill);
            double expForNext = experienceManager.getExperienceForNextLevel(player, skill);
            
            player.sendMessage("§6" + skill.getIcon() + " " + skill.getDisplayName());
            player.sendMessage("  §7Level: §e" + level);
            player.sendMessage("  §7Total Experience: §b" + String.format("%.1f", totalExp));
            player.sendMessage("  §7Experience for Next Level: §b" + String.format("%.1f", expForNext));
            player.sendMessage("  §7Progress: §b" + String.format("%.1f", progress) + "%");
            
            // Show bonuses for each skill
            player.sendMessage("  §7Bonuses:");
            
            LevelingPlugin plugin = getPlugin();
            
            if (skill == SkillType.MINING) {
                double doubleDropPercent = Math.min(100.0, level * plugin.getConfigManager().getMiningDoubleDropChancePerLevel() * 100);
                player.sendMessage("    §7+§a" + String.format("%.1f", doubleDropPercent) + "% Double Drop Chance");
            } else if (skill == SkillType.EXCAVATION) {
                double doubleDropPercent = Math.min(100.0, level * plugin.getConfigManager().getExcavationDoubleDropChancePerLevel() * 100);
                player.sendMessage("    §7+§a" + String.format("%.1f", doubleDropPercent) + "% Double Drop Chance");
            } else if (skill == SkillType.FARMING) {
                double doubleDropPercent = Math.min(100.0, level * plugin.getConfigManager().getFarmingDoubleDropChancePerLevel() * 100);
                player.sendMessage("    §7+§a" + String.format("%.1f", doubleDropPercent) + "% Double Drop Chance");
                int autoReplantLevel = plugin.getConfigManager().getFarmingAutoReplantLevel();
                if (level >= autoReplantLevel) {
                    player.sendMessage("    §7+§aAuto Replant §7(enabled)");
                } else {
                    player.sendMessage("    §7+§aAuto Replant §7(level " + autoReplantLevel + "+)");
                }
            } else if (skill == SkillType.COMBAT) {
                double damagePercent = Math.min(25.0, level * 0.5); // 0.5% per level, max 25%
                player.sendMessage("    §7+§c" + String.format("%.1f", damagePercent) + "% Attack Damage");
            } else if (skill == SkillType.DEFENSE) {
                double toughnessPercent = Math.min(25.0, level * 0.5); // 0.5% per level, max 25%
                player.sendMessage("    §7+§9" + String.format("%.1f", toughnessPercent) + "% Armor Toughness");
            } else if (skill == SkillType.ACROBATICS) {
                double fallReductionPercent = Math.min(50.0, level * plugin.getConfigManager().getAcrobaticsDamageReductionPerLevel() * 100);
                player.sendMessage("    §7+§b" + String.format("%.1f", fallReductionPercent) + "% Fall Damage Reduction");
            } else if (skill == SkillType.SMITHING) {
                double doubleCraftChance = Math.min(5.0, level * plugin.getConfigManager().getSmithingResourceReturnChance() * 100);
                player.sendMessage("    §7+§e" + String.format("%.2f", doubleCraftChance) + "% Double Craft Chance");
            }
            
            player.sendMessage("");
        }
        
        return true;
    }
}

