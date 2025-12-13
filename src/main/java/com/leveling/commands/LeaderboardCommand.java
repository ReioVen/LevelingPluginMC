package com.leveling.commands;

import com.leveling.LevelingPlugin;
import com.leveling.gui.LeaderboardGUI;
import com.leveling.managers.SkillManager;
import com.leveling.models.SkillType;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class LeaderboardCommand implements CommandExecutor {
    private final LevelingPlugin plugin;
    private final SkillManager skillManager;
    
    public LeaderboardCommand(LevelingPlugin plugin, SkillManager skillManager) {
        this.plugin = plugin;
        this.skillManager = skillManager;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cThis command can only be used by players!");
            return true;
        }
        
        Player player = (Player) sender;
        
        if (args.length == 0) {
            player.sendMessage("§cUsage: /leaderboard <skill> [page]");
            player.sendMessage("§7Available skills: " + getSkillList());
            return true;
        }
        
        SkillType skill;
        try {
            skill = SkillType.valueOf(args[0].toUpperCase());
        } catch (IllegalArgumentException e) {
            player.sendMessage("§cInvalid skill: " + args[0]);
            player.sendMessage("§7Available skills: " + getSkillList());
            return true;
        }
        
        int page = 1;
        if (args.length > 1) {
            try {
                page = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                player.sendMessage("§cInvalid page number: " + args[1]);
                return true;
            }
        }
        
        // Open leaderboard GUI
        new LeaderboardGUI(plugin, skillManager).openGUI(player, skill, page);
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
