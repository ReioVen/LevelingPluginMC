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
        
        // Get current stored XP (starts at 0 for each level)
        double storedExp = data.getExperience(skill);
        
        // Keep leveling up as long as we have enough XP for the next level
        while (currentLevel < maxLevel) {
            // Get XP required for the next level (non-cumulative, specific to this level)
            double expForNextLevel = getExperienceRequired(currentLevel + 1);
            
            // Check if we have enough XP to level up
            if (storedExp >= expForNextLevel) {
                // Level up!
                currentLevel++;
                
                // Reset XP to 0 (no excess XP carried over)
                data.setLevel(skill, currentLevel);
                data.setExperience(skill, 0.0);
                
                // Update storedExp to 0 for next iteration check
                storedExp = 0.0;
                
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
    
    /**
     * Get experience required for a specific level (non-cumulative).
     * Each level requires progressively more XP.
     * Formula: base * (level ^ multiplier)
     * Example: Level 2 needs base * (2^multiplier), Level 3 needs base * (3^multiplier), etc.
     */
    public double getExperienceRequired(int level) {
        if (level <= 1) return 0;
        // Non-cumulative: each level requires base * (level ^ multiplier)
        // This makes each level progressively harder
        return config.getBaseExp() * Math.pow(level, config.getExpMultiplier());
    }
    
    public double getExperienceForNextLevel(Player player, SkillType skill) {
        PlayerSkillData data = skillManager.getPlayerData(player);
        int currentLevel = data.getLevel(skill);
        if (currentLevel >= config.getMaxLevel()) {
            return 0;
        }
        // Return the XP needed for the next level (non-cumulative)
        return getExperienceRequired(currentLevel + 1);
    }
    
    /**
     * Get experience needed to reach next level.
     * Since XP resets to 0 on level up, this is just the XP required for the next level.
     */
    public double getExperienceNeededForNextLevel(Player player, SkillType skill) {
        PlayerSkillData data = skillManager.getPlayerData(player);
        int currentLevel = data.getLevel(skill);
        if (currentLevel >= config.getMaxLevel()) {
            return 0;
        }
        // XP resets to 0 on level up, so we just need the XP for the next level
        return getExperienceRequired(currentLevel + 1);
    }
    
    public double getProgressPercentage(Player player, SkillType skill) {
        PlayerSkillData data = skillManager.getPlayerData(player);
        int currentLevel = data.getLevel(skill);
        if (currentLevel >= config.getMaxLevel()) {
            return 100.0;
        }
        
        // Get current stored XP (starts at 0 for each level)
        double storedExp = data.getExperience(skill);
        
        // Get XP needed for next level (non-cumulative)
        double expForNextLevel = getExperienceRequired(currentLevel + 1);
        
        if (expForNextLevel <= 0) return 100.0;
        
        // Calculate percentage: (current XP / XP needed for next level) * 100
        double percentage = (storedExp / expForNextLevel) * 100.0;
        return Math.min(100.0, Math.max(0.0, percentage));
    }
    
    /**
     * Get total experience for a skill.
     * Since XP resets to 0 on level up, this returns the current stored XP for the current level.
     */
    public double getTotalExperience(Player player, SkillType skill) {
        PlayerSkillData data = skillManager.getPlayerData(player);
        // XP resets to 0 on level up, so total XP is just the stored XP for current level
        return data.getExperience(skill);
    }
}

