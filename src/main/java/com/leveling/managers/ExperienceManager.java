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
        
        // Check for level up - we need to compare TOTAL XP, not just stored XP
        // Stored XP is the excess after leveling, so we need to add it to the current level's requirement
        double storedExp = data.getExperience(skill);
        double expForCurrentLevel = getExperienceRequired(currentLevel);
        double totalExp = storedExp + expForCurrentLevel;
        
        // Keep leveling up as long as we have enough total XP
        while (currentLevel < maxLevel) {
            double expForNextLevel = getExperienceRequired(currentLevel + 1);
            
            // Check if total XP is greater than or equal to the next level requirement
            if (totalExp >= expForNextLevel) {
                // Level up!
                currentLevel++;
                
                // Calculate new stored XP (excess after leveling)
                // The new stored XP is the total XP minus what was required for the new level
                double newStoredExp = totalExp - expForNextLevel;
                
                // Update level and stored experience (reset to excess XP after leveling)
                data.setLevel(skill, currentLevel);
                data.setExperience(skill, newStoredExp);
                
                // Recalculate totalExp for next iteration (in case we can level up multiple times)
                // After leveling, the new totalExp is: storedExp + required for new level
                expForCurrentLevel = expForNextLevel;
                totalExp = newStoredExp + expForCurrentLevel;
                
                // Level up!
                onLevelUp(player, skill, currentLevel);
                
                // Update bonuses if defense or combat leveled up
                if (skill == SkillType.DEFENSE) {
                    skillManager.updateDefenseBonuses(player);
                } else if (skill == SkillType.COMBAT) {
                    skillManager.updateCombatBonuses(player);
                }
            } else {
                // Not enough XP to level up, break the loop
                break;
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
        // Return the total XP needed for next level (not just the difference)
        return getExperienceRequired(currentLevel + 1);
    }
    
    /**
     * Get experience needed to reach next level (difference between current and next level)
     */
    public double getExperienceNeededForNextLevel(Player player, SkillType skill) {
        PlayerSkillData data = skillManager.getPlayerData(player);
        int currentLevel = data.getLevel(skill);
        if (currentLevel >= config.getMaxLevel()) {
            return 0;
        }
        double expForNextLevel = getExperienceRequired(currentLevel + 1);
        double expForCurrentLevel = getExperienceRequired(currentLevel);
        return expForNextLevel - expForCurrentLevel;
    }
    
    public double getProgressPercentage(Player player, SkillType skill) {
        PlayerSkillData data = skillManager.getPlayerData(player);
        int currentLevel = data.getLevel(skill);
        if (currentLevel >= config.getMaxLevel()) {
            return 100.0;
        }
        
        // Calculate total XP: stored XP (excess after leveling) + XP required for current level
        double storedExp = data.getExperience(skill);
        double expForCurrentLevel = getExperienceRequired(currentLevel);
        double totalExp = storedExp + expForCurrentLevel;
        
        // Calculate XP needed for next level
        double expForNextLevel = getExperienceRequired(currentLevel + 1);
        double expNeeded = expForNextLevel - expForCurrentLevel;
        
        if (expNeeded <= 0) return 100.0;
        
        // Calculate progress: how much exp we have towards the next level
        double expProgress = totalExp - expForCurrentLevel;
        if (expProgress < 0) expProgress = 0;
        
        // Calculate percentage: (current XP in this level / XP needed for this level) * 100
        double percentage = (expProgress / expNeeded) * 100.0;
        return Math.min(100.0, Math.max(0.0, percentage));
    }
    
    /**
     * Get total experience for a skill (stored + required for current level)
     */
    public double getTotalExperience(Player player, SkillType skill) {
        PlayerSkillData data = skillManager.getPlayerData(player);
        int currentLevel = data.getLevel(skill);
        double storedExp = data.getExperience(skill);
        double expForCurrentLevel = getExperienceRequired(currentLevel);
        return storedExp + expForCurrentLevel;
    }
}

