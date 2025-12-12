package com.leveling.commands;

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
            double exp = skillManager.getExperience(player, skill);
            double progress = experienceManager.getProgressPercentage(player, skill);
            double expForNext = experienceManager.getExperienceForNextLevel(player, skill);
            
            player.sendMessage("§6" + skill.getIcon() + " " + skill.getDisplayName());
            player.sendMessage("  §7Level: §e" + level);
            player.sendMessage("  §7Current Experience: §b" + String.format("%.1f", exp));
            player.sendMessage("  §7Experience for Next Level: §b" + String.format("%.1f", expForNext));
            player.sendMessage("  §7Progress: §b" + String.format("%.1f", progress) + "%");
            
            if (skill == SkillType.DEFENSE) {
                // Show defense bonuses
                double toughnessPercent = Math.min(25.0, level * 0.5); // 0.5% per level, max 25%
                
                player.sendMessage("  §7Bonuses:");
                player.sendMessage("    §7+§9" + String.format("%.1f", toughnessPercent) + "% Armor Toughness");
            } else if (skill == SkillType.COMBAT) {
                // Show combat bonuses
                double damagePercent = Math.min(25.0, level * 0.5); // 0.5% per level, max 25%
                
                player.sendMessage("  §7Bonuses:");
                player.sendMessage("    §7+§c" + String.format("%.1f", damagePercent) + "% Attack Damage");
            }
            
            player.sendMessage("");
        }
        
        return true;
    }
}

