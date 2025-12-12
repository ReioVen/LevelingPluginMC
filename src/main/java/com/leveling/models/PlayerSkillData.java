package com.leveling.models;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerSkillData {
    private final UUID playerUUID;
    private final Map<SkillType, Integer> levels;
    private final Map<SkillType, Double> experience;
    
    public PlayerSkillData(UUID playerUUID) {
        this.playerUUID = playerUUID;
        this.levels = new HashMap<>();
        this.experience = new HashMap<>();
        
        // Initialize all skills to level 1
        for (SkillType skill : SkillType.values()) {
            levels.put(skill, 1);
            experience.put(skill, 0.0);
        }
    }
    
    public PlayerSkillData(UUID playerUUID, Map<SkillType, Integer> levels, Map<SkillType, Double> experience) {
        this.playerUUID = playerUUID;
        this.levels = levels != null ? levels : new HashMap<>();
        this.experience = experience != null ? experience : new HashMap<>();
        
        // Ensure all skills are initialized
        for (SkillType skill : SkillType.values()) {
            if (!this.levels.containsKey(skill)) {
                this.levels.put(skill, 1);
            }
            if (!this.experience.containsKey(skill)) {
                this.experience.put(skill, 0.0);
            }
        }
    }
    
    public UUID getPlayerUUID() {
        return playerUUID;
    }
    
    public int getLevel(SkillType skill) {
        return levels.getOrDefault(skill, 1);
    }
    
    public void setLevel(SkillType skill, int level) {
        levels.put(skill, level);
    }
    
    public double getExperience(SkillType skill) {
        return experience.getOrDefault(skill, 0.0);
    }
    
    public void setExperience(SkillType skill, double exp) {
        experience.put(skill, exp);
    }
    
    public void addExperience(SkillType skill, double exp) {
        double current = getExperience(skill);
        setExperience(skill, current + exp);
    }
    
    public Map<SkillType, Integer> getLevels() {
        return new HashMap<>(levels);
    }
    
    public Map<SkillType, Double> getExperience() {
        return new HashMap<>(experience);
    }
}

