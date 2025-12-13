package com.leveling.commands;

import com.leveling.LevelingPlugin;
import com.leveling.gui.SkillStatsGUI;
import com.leveling.managers.ExperienceManager;
import com.leveling.managers.SkillManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SkillStatsCommand implements CommandExecutor {
    private final LevelingPlugin plugin;
    private final SkillManager skillManager;
    private final ExperienceManager experienceManager;
    
    public SkillStatsCommand(LevelingPlugin plugin, SkillManager skillManager, ExperienceManager experienceManager) {
        this.plugin = plugin;
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
        
        // Open stats GUI
        new SkillStatsGUI(plugin, skillManager, experienceManager).openGUI(player);
        return true;
    }
}
