package com.leveling;

import com.leveling.commands.SkillsCommand;
import com.leveling.commands.SkillStatsCommand;
import com.leveling.listeners.*;
import com.leveling.managers.*;
import org.bukkit.plugin.java.JavaPlugin;

public class LevelingPlugin extends JavaPlugin {
    
    private SkillManager skillManager;
    private ExperienceManager experienceManager;
    private DataManager dataManager;
    private HUDManager hudManager;
    private ConfigManager configManager;
    
    @Override
    public void onEnable() {
        // Save default config
        saveDefaultConfig();
        
        // Initialize managers
        configManager = new ConfigManager(this);
        dataManager = new DataManager(this);
        skillManager = new SkillManager(this);
        experienceManager = new ExperienceManager(this, skillManager);
        hudManager = new HUDManager(this, skillManager, experienceManager);
        
        // Register event listeners
        getServer().getPluginManager().registerEvents(new PlayerJoinQuitListener(this), this);
        getServer().getPluginManager().registerEvents(new MiningListener(this, experienceManager), this);
        getServer().getPluginManager().registerEvents(new ExcavationListener(this, experienceManager), this);
        getServer().getPluginManager().registerEvents(new WoodcuttingListener(this, experienceManager), this);
        getServer().getPluginManager().registerEvents(new CombatListener(this, experienceManager), this);
        getServer().getPluginManager().registerEvents(new DefenseListener(this, experienceManager, skillManager), this);
        getServer().getPluginManager().registerEvents(new FarmingListener(this, experienceManager), this);
        getServer().getPluginManager().registerEvents(new SmithingListener(this, experienceManager), this);
        getServer().getPluginManager().registerEvents(new AcrobaticsListener(this, experienceManager), this);
        
        // Register commands
        getCommand("level").setExecutor(new SkillsCommand(this, skillManager, experienceManager));
        getCommand("levelstats").setExecutor(new com.leveling.commands.SkillStatsCommand(skillManager, experienceManager));
        getCommand("leaderboard").setExecutor(new com.leveling.commands.LeaderboardCommand(this, skillManager));
        
        // Start HUD update task
        hudManager.startHUDTask();
        
        getLogger().info("LevelingPlugin has been enabled!");
    }
    
    @Override
    public void onDisable() {
        // Save all player data
        if (dataManager != null) {
            dataManager.saveAllPlayers();
        }
        
        // Stop HUD task
        if (hudManager != null) {
            hudManager.stopHUDTask();
        }
        
        getLogger().info("LevelingPlugin has been disabled!");
    }
    
    public SkillManager getSkillManager() {
        return skillManager;
    }
    
    public ExperienceManager getExperienceManager() {
        return experienceManager;
    }
    
    public DataManager getDataManager() {
        return dataManager;
    }
    
    public HUDManager getHUDManager() {
        return hudManager;
    }
    
    public ConfigManager getConfigManager() {
        return configManager;
    }
}

