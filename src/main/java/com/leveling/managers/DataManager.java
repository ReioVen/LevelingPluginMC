package com.leveling.managers;

import com.leveling.LevelingPlugin;
import com.leveling.models.PlayerSkillData;
import com.leveling.models.SkillType;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class DataManager {
    private final LevelingPlugin plugin;
    private final File dataFile;
    
    public DataManager(LevelingPlugin plugin) {
        this.plugin = plugin;
        this.dataFile = new File(plugin.getDataFolder(), "playerdata.yml");
        
        if (!dataFile.exists()) {
            try {
                dataFile.getParentFile().mkdirs();
                dataFile.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().severe("Failed to create playerdata.yml: " + e.getMessage());
            }
        }
    }
    
    public void loadPlayerData(Player player) {
        FileConfiguration config = YamlConfiguration.loadConfiguration(dataFile);
        String uuidString = player.getUniqueId().toString();
        
        if (!config.contains("players." + uuidString)) {
            return; // New player, will use defaults
        }
        
        Map<SkillType, Integer> levels = new HashMap<>();
        Map<SkillType, Double> experience = new HashMap<>();
        
        for (SkillType skill : SkillType.values()) {
            String skillKey = skill.name().toLowerCase();
            int level = config.getInt("players." + uuidString + ".skills." + skillKey + ".level", 1);
            double exp = config.getDouble("players." + uuidString + ".skills." + skillKey + ".experience", 0.0);
            
            levels.put(skill, level);
            experience.put(skill, exp);
        }
        
        PlayerSkillData data = new PlayerSkillData(player.getUniqueId(), levels, experience);
        plugin.getSkillManager().setPlayerData(player.getUniqueId(), data);
    }
    
    public void savePlayerData(Player player) {
        PlayerSkillData data = plugin.getSkillManager().getPlayerData(player);
        if (data == null) {
            return;
        }
        
        FileConfiguration config = YamlConfiguration.loadConfiguration(dataFile);
        String uuidString = player.getUniqueId().toString();
        
        for (SkillType skill : SkillType.values()) {
            String skillKey = skill.name().toLowerCase();
            config.set("players." + uuidString + ".skills." + skillKey + ".level", data.getLevel(skill));
            config.set("players." + uuidString + ".skills." + skillKey + ".experience", data.getExperience(skill));
        }
        
        try {
            config.save(dataFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Failed to save player data for " + player.getName() + ": " + e.getMessage());
        }
    }
    
    public void saveAllPlayers() {
        for (Player player : plugin.getServer().getOnlinePlayers()) {
            savePlayerData(player);
        }
        
        // Also save offline players if needed
        Map<UUID, PlayerSkillData> allData = plugin.getSkillManager().getAllPlayerData();
        FileConfiguration config = YamlConfiguration.loadConfiguration(dataFile);
        
        for (Map.Entry<UUID, PlayerSkillData> entry : allData.entrySet()) {
            UUID uuid = entry.getKey();
            PlayerSkillData data = entry.getValue();
            String uuidString = uuid.toString();
            
            for (SkillType skill : SkillType.values()) {
                String skillKey = skill.name().toLowerCase();
                config.set("players." + uuidString + ".skills." + skillKey + ".level", data.getLevel(skill));
                config.set("players." + uuidString + ".skills." + skillKey + ".experience", data.getExperience(skill));
            }
        }
        
        try {
            config.save(dataFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Failed to save all player data: " + e.getMessage());
        }
    }
    
    public void loadOfflinePlayerData(UUID uuid) {
        FileConfiguration config = YamlConfiguration.loadConfiguration(dataFile);
        String uuidString = uuid.toString();
        
        if (!config.contains("players." + uuidString)) {
            return; // New player, will use defaults
        }
        
        Map<SkillType, Integer> levels = new HashMap<>();
        Map<SkillType, Double> experience = new HashMap<>();
        
        for (SkillType skill : SkillType.values()) {
            String skillKey = skill.name().toLowerCase();
            int level = config.getInt("players." + uuidString + ".skills." + skillKey + ".level", 1);
            double exp = config.getDouble("players." + uuidString + ".skills." + skillKey + ".experience", 0.0);
            
            levels.put(skill, level);
            experience.put(skill, exp);
        }
        
        PlayerSkillData data = new PlayerSkillData(uuid, levels, experience);
        plugin.getSkillManager().setPlayerData(uuid, data);
    }
    
    public void saveOfflinePlayerData(UUID uuid, PlayerSkillData data) {
        if (data == null) {
            return;
        }
        
        FileConfiguration config = YamlConfiguration.loadConfiguration(dataFile);
        String uuidString = uuid.toString();
        
        for (SkillType skill : SkillType.values()) {
            String skillKey = skill.name().toLowerCase();
            config.set("players." + uuidString + ".skills." + skillKey + ".level", data.getLevel(skill));
            config.set("players." + uuidString + ".skills." + skillKey + ".experience", data.getExperience(skill));
        }
        
        try {
            config.save(dataFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Failed to save offline player data for " + uuid + ": " + e.getMessage());
        }
    }
}

