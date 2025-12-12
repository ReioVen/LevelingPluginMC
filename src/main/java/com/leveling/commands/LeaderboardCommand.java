package com.leveling.commands;

import com.leveling.LevelingPlugin;
import com.leveling.managers.SkillManager;
import com.leveling.models.PlayerSkillData;
import com.leveling.models.SkillType;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.*;

public class LeaderboardCommand implements CommandExecutor {
    private final LevelingPlugin plugin;
    private final SkillManager skillManager;
    
    public LeaderboardCommand(LevelingPlugin plugin, SkillManager skillManager) {
        this.plugin = plugin;
        this.skillManager = skillManager;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sender.sendMessage("§cUsage: /leaderboard <skill> [page]");
            sender.sendMessage("§7Available skills: " + getSkillList());
            return true;
        }
        
        SkillType skill;
        try {
            skill = SkillType.valueOf(args[0].toUpperCase());
        } catch (IllegalArgumentException e) {
            sender.sendMessage("§cInvalid skill: " + args[0]);
            sender.sendMessage("§7Available skills: " + getSkillList());
            return true;
        }
        
        int page = 1;
        if (args.length > 1) {
            try {
                page = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                sender.sendMessage("§cInvalid page number: " + args[1]);
                return true;
            }
        }
        
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
            sender.sendMessage("§cNo players found for " + skill.getDisplayName() + " leaderboard.");
            return true;
        }
        
        int pageSize = 10;
        int totalPages = (int) Math.ceil((double) leaderboard.size() / pageSize);
        page = Math.max(1, Math.min(page, totalPages));
        
        int start = (page - 1) * pageSize;
        int end = Math.min(start + pageSize, leaderboard.size());
        
        sender.sendMessage("§6§l=== " + skill.getIcon() + " " + skill.getDisplayName() + " Leaderboard ===");
        sender.sendMessage("§7Page §e" + page + "§7/§e" + totalPages);
        sender.sendMessage("");
        
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
            sender.sendMessage(String.format("§7#%d %s%s §7- Level §e%d", 
                i + 1, rankColor, playerName, level));
        }
        
        return true;
    }
    
    private String getRankColor(int rank) {
        if (rank == 1) return "§6§l"; // Gold for #1
        if (rank == 2) return "§7§l"; // Silver for #2
        if (rank == 3) return "§c§l"; // Bronze for #3
        return "§f"; // White for others
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

