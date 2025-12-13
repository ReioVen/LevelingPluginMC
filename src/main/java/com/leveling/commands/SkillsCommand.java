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

import java.util.*;
import java.util.AbstractMap;

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
            sender.sendMessage("§7/level <skill> §8- §7View specific skill details + leaderboard");
            sender.sendMessage("§7/level leaderboard <skill> [page] §8- §7View leaderboard for a skill");
            sender.sendMessage("§7  §7Aliases: §e/level lb <skill> §7or §e/level top <skill>");
            sender.sendMessage("§7/levelstats §8- §7View detailed statistics with bonuses");
            sender.sendMessage("§7/leaderboard <skill> [page] §8- §7View leaderboard (standalone)");
            sender.sendMessage("§7  §7Aliases: §e/lb <skill> §7or §e/top <skill>");
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
            // Check if it's a leaderboard command
            if (args[0].equalsIgnoreCase("leaderboard") || args[0].equalsIgnoreCase("lb") || args[0].equalsIgnoreCase("top")) {
                if (args.length < 2) {
                    player.sendMessage("§cUsage: /level leaderboard <skill> [page]");
                    player.sendMessage("§7Available skills: " + getSkillList());
                    return true;
                }
                
                SkillType skill;
                try {
                    skill = SkillType.valueOf(args[1].toUpperCase());
                } catch (IllegalArgumentException e) {
                    player.sendMessage("§cInvalid skill: " + args[1]);
                    player.sendMessage("§7Available skills: " + getSkillList());
                    return true;
                }
                
                int page = 1;
                if (args.length > 2) {
                    try {
                        page = Integer.parseInt(args[2]);
                    } catch (NumberFormatException e) {
                        player.sendMessage("§cInvalid page number: " + args[2]);
                        return true;
                    }
                }
                
                // Show leaderboard for the skill
                showLeaderboard(player, skill, page);
                return true;
            }
            
            // Show specific skill
            try {
                SkillType skill = SkillType.valueOf(args[0].toUpperCase());
                int level = skillManager.getLevel(player, skill);
                double totalExp = experienceManager.getTotalExperience(player, skill);
                double progress = experienceManager.getProgressPercentage(player, skill);
                double expForNext = experienceManager.getExperienceForNextLevel(player, skill);
                
                player.sendMessage("§6" + skill.getIcon() + " " + skill.getDisplayName());
                player.sendMessage("§7Level: §e" + level);
                player.sendMessage("§7Experience: §b" + String.format("%.1f", totalExp) + "§7/§b" + String.format("%.1f", expForNext));
                player.sendMessage("§7Progress: §b" + String.format("%.1f", progress) + "%");
                
                // Show leaderboard for this skill
                player.sendMessage("");
                showLeaderboard(player, skill, 1);
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
    
    private void showLeaderboard(Player player, SkillType skill, int page) {
        // Get all player data and sort by skill level
        Map<UUID, PlayerSkillData> allData = skillManager.getAllPlayerData();
        List<Map.Entry<UUID, Integer>> leaderboard = new ArrayList<>();
        
        for (Map.Entry<UUID, PlayerSkillData> entry : allData.entrySet()) {
            int level = entry.getValue().getLevel(skill);
            if (level > 1) { // Only show players with level > 1
                leaderboard.add(new AbstractMap.SimpleEntry<>(entry.getKey(), level));
            }
        }
        
        // Sort by level descending
        leaderboard.sort((a, b) -> Integer.compare(b.getValue(), a.getValue()));
        
        if (leaderboard.isEmpty()) {
            player.sendMessage("§cNo players found for " + skill.getDisplayName() + " leaderboard.");
            return;
        }
        
        int pageSize = 10;
        int totalPages = (int) Math.ceil((double) leaderboard.size() / pageSize);
        page = Math.max(1, Math.min(page, totalPages));
        
        int start = (page - 1) * pageSize;
        int end = Math.min(start + pageSize, leaderboard.size());
        
        player.sendMessage("§6§l=== " + skill.getIcon() + " " + skill.getDisplayName() + " Leaderboard ===");
        player.sendMessage("§7Page §e" + page + "§7/§e" + totalPages);
        player.sendMessage("");
        
        for (int i = start; i < end; i++) {
            Map.Entry<UUID, Integer> entry = leaderboard.get(i);
            UUID uuid = entry.getKey();
            int level = entry.getValue();
            
            String playerName = "Unknown";
            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);
            if (offlinePlayer.hasPlayedBefore() || offlinePlayer.isOnline()) {
                playerName = offlinePlayer.getName();
            }
            
            String rankColor = getRankColor(i + 1);
            player.sendMessage(String.format("§7#%d %s%s §7- Level §e%d", 
                i + 1, rankColor, playerName, level));
        }
    }
    
    private String getRankColor(int rank) {
        if (rank == 1) return "§6§l"; // Gold for #1
        if (rank == 2) return "§7§l"; // Silver for #2
        if (rank == 3) return "§c§l"; // Bronze for #3
        return "§f"; // White for others
    }
}

