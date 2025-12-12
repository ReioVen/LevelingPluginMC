package com.leveling.managers;

import com.leveling.LevelingPlugin;
import com.leveling.models.PlayerSkillData;
import com.leveling.models.SkillType;
import org.bukkit.entity.Player;

public class ExperienceManager {
    private final LevelingPlugin plugin;
    private final SkillManager skillManager;
    private final ConfigManager config;
    
    public ExperienceManager(LevelingPlugin plugin, SkillManager skillManager) {
        this.plugin = plugin;
        this.skillManager = skillManager;
        this.config = plugin.getConfigManager();
    }
    
    public void addExperience(Player player, SkillType skill, double amount) {
        if (amount <= 0) return;
        
        PlayerSkillData data = skillManager.getPlayerData(player);
        int currentLevel = data.getLevel(skill);
        int maxLevel = config.getMaxLevel();
        
        if (currentLevel >= maxLevel) {
            return; // Already at max level
        }
        
        // Add experience
        data.addExperience(skill, amount);
        
        // Check for level up
        double currentExp = data.getExperience(skill);
        double expForNextLevel = getExperienceRequired(currentLevel + 1);
        
        while (currentExp >= expForNextLevel && currentLevel < maxLevel) {
            currentLevel++;
            currentExp -= expForNextLevel;
            data.setLevel(skill, currentLevel);
            data.setExperience(skill, currentExp);
            
            // Level up!
            onLevelUp(player, skill, currentLevel);
            
            // Update bonuses if defense or combat leveled up
            if (skill == SkillType.DEFENSE) {
                skillManager.updateDefenseBonuses(player);
            } else if (skill == SkillType.COMBAT) {
                skillManager.updateCombatBonuses(player);
            }
            
            // Check if we can level up again
            if (currentLevel < maxLevel) {
                expForNextLevel = getExperienceRequired(currentLevel + 1);
            }
        }
    }
    
    private void onLevelUp(Player player, SkillType skill, int newLevel) {
        // Send level up message
        player.sendMessage("§a§lLEVEL UP! §r§7" + skill.getIcon() + " " + skill.getDisplayName() + " is now level " + newLevel + "!");
        
        // Bonuses are updated in addExperience method
        
        // Trigger level up event (if you want to add custom events)
        // Bukkit.getPluginManager().callEvent(new SkillLevelUpEvent(player, skill, newLevel));
    }
    
    public double getExperienceRequired(int level) {
        if (level <= 1) return 0;
        return config.getBaseExp() * Math.pow(level - 1, config.getExpMultiplier());
    }
    
    public double getExperienceForNextLevel(Player player, SkillType skill) {
        PlayerSkillData data = skillManager.getPlayerData(player);
        int currentLevel = data.getLevel(skill);
        if (currentLevel >= config.getMaxLevel()) {
            return 0;
        }
        return getExperienceRequired(currentLevel + 1);
    }
    
    public double getProgressPercentage(Player player, SkillType skill) {
        PlayerSkillData data = skillManager.getPlayerData(player);
        int currentLevel = data.getLevel(skill);
        if (currentLevel >= config.getMaxLevel()) {
            return 100.0;
        }
        
        double currentExp = data.getExperience(skill);
        double expForNextLevel = getExperienceRequired(currentLevel + 1);
        double expForCurrentLevel = getExperienceRequired(currentLevel);
        double expNeeded = expForNextLevel - expForCurrentLevel;
        
        if (expNeeded <= 0) return 100.0;
        
        // Calculate progress: how much exp we have towards the next level
        double expProgress = currentExp - expForCurrentLevel;
        if (expProgress < 0) expProgress = 0;
        
        double percentage = (expProgress / expNeeded) * 100.0;
        return Math.min(100.0, Math.max(0.0, percentage));
    }
}

