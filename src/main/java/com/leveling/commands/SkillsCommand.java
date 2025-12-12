package com.leveling.commands;

import com.leveling.LevelingPlugin;
import com.leveling.managers.ExperienceManager;
import com.leveling.managers.SkillManager;
import com.leveling.models.PlayerSkillData;
import com.leveling.models.SkillType;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class SkillsCommand implements CommandExecutor {
    private final LevelingPlugin plugin;
    private final SkillManager skillManager;
    private final ExperienceManager experienceManager;
    
    public SkillsCommand(LevelingPlugin plugin, SkillManager skillManager, ExperienceManager experienceManager) {
        this.plugin = plugin;
        this.skillManager = skillManager;
        this.experienceManager = experienceManager;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // Admin command: /level set <username> <skill> <level>
        if (args.length >= 4 && args[0].equalsIgnoreCase("set")) {
            if (!sender.hasPermission("leveling.admin")) {
                sender.sendMessage("§cYou don't have permission to use this command!");
                return true;
            }
            
            String targetName = args[1];
            String skillName = args[2].toUpperCase();
            int level;
            
            try {
                level = Integer.parseInt(args[3]);
            } catch (NumberFormatException e) {
                sender.sendMessage("§cInvalid level: " + args[3]);
                return true;
            }
            
            SkillType skill;
            try {
                skill = SkillType.valueOf(skillName);
            } catch (IllegalArgumentException e) {
                sender.sendMessage("§cInvalid skill: " + skillName);
                sender.sendMessage("§7Available skills: " + getSkillList());
                return true;
            }
            
            // Get player (online or offline)
            Player targetPlayer = Bukkit.getPlayer(targetName);
            
            if (targetPlayer != null) {
                // Online player
                skillManager.setLevel(targetPlayer, skill, level);
                skillManager.updateDefenseBonuses(targetPlayer);
                plugin.getDataManager().savePlayerData(targetPlayer);
                sender.sendMessage("§aSet " + targetName + "'s " + skill.getDisplayName() + " to level " + level);
                targetPlayer.sendMessage("§aYour " + skill.getDisplayName() + " level has been set to " + level + " by " + sender.getName());
            } else {
                // Try to get offline player
                OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(targetName);
                if (offlinePlayer.hasPlayedBefore() || offlinePlayer.isOnline()) {
                    UUID targetUUID = offlinePlayer.getUniqueId();
                    // Load data from file and set level
                    plugin.getDataManager().loadOfflinePlayerData(targetUUID);
                    PlayerSkillData data = skillManager.getPlayerData(targetUUID);
                    data.setLevel(skill, level);
                    plugin.getDataManager().saveOfflinePlayerData(targetUUID, data);
                    sender.sendMessage("§aSet " + targetName + "'s " + skill.getDisplayName() + " to level " + level + " (offline player)");
                } else {
                    sender.sendMessage("§cPlayer not found: " + targetName);
                }
            }
            
            return true;
        }
        
        // Help command
        if (args.length > 0 && (args[0].equalsIgnoreCase("help") || args[0].equalsIgnoreCase("?"))) {
            sender.sendMessage("§6§l=== Level Commands ===");
            sender.sendMessage("§7/level §8- §7View all your skill levels");
            sender.sendMessage("§7/level <skill> §8- §7View specific skill details");
            sender.sendMessage("§7/levelstats §8- §7View detailed statistics");
            if (sender.hasPermission("leveling.admin")) {
                sender.sendMessage("§7/level set <player> <skill> <level> §8- §7Set a player's skill level (Admin)");
            }
            sender.sendMessage("§7");
            sender.sendMessage("§7Available skills: " + getSkillList());
            return true;
        }
        
        // Regular player commands
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cThis command can only be used by players!");
            return true;
        }
        
        Player player = (Player) sender;
        
        if (args.length > 0) {
            // Show specific skill
            try {
                SkillType skill = SkillType.valueOf(args[0].toUpperCase());
                int level = skillManager.getLevel(player, skill);
                double exp = skillManager.getExperience(player, skill);
                double progress = experienceManager.getProgressPercentage(player, skill);
                double expForNext = experienceManager.getExperienceForNextLevel(player, skill);
                
                player.sendMessage("§6" + skill.getIcon() + " " + skill.getDisplayName());
                player.sendMessage("§7Level: §e" + level);
                player.sendMessage("§7Experience: §b" + String.format("%.1f", exp) + "§7/§b" + String.format("%.1f", expForNext));
                player.sendMessage("§7Progress: §b" + String.format("%.1f", progress) + "%");
            } catch (IllegalArgumentException e) {
                player.sendMessage("§cInvalid skill: " + args[0]);
                player.sendMessage("§7Available skills: " + getSkillList());
                player.sendMessage("§7Use §e/level help §7for more information.");
            }
        } else {
            // Show all skills
            player.sendMessage("§6§l=== Your Levels ===");
            for (SkillType skill : SkillType.values()) {
                int level = skillManager.getLevel(player, skill);
                double progress = experienceManager.getProgressPercentage(player, skill);
                player.sendMessage(String.format("§7%s %s §7- Level §e%d §7(§b%.1f%%§7)", 
                    skill.getIcon(), skill.getDisplayName(), level, progress));
            }
        }
        
        return true;
    }
    
    private String getSkillList() {
        StringBuilder sb = new StringBuilder();
        SkillType[] skills = SkillType.values();
        for (int i = 0; i < skills.length; i++) {
            sb.append(skills[i].name());
            if (i < skills.length - 1) {
                sb.append(", ");
            }
        }
        return sb.toString();
    }
}

