package com.leveling.listeners;

import com.leveling.LevelingPlugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerJoinQuitListener implements Listener {
    private final LevelingPlugin plugin;
    
    public PlayerJoinQuitListener(LevelingPlugin plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        // Load player data when they join
        plugin.getDataManager().loadPlayerData(event.getPlayer());
        
        // Clean up any old modifiers and update bonuses
        // Schedule on next tick to ensure player is fully loaded
        plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
            plugin.getSkillManager().updateDefenseBonuses(event.getPlayer());
            plugin.getSkillManager().updateCombatBonuses(event.getPlayer());
        }, 1L);
    }
    
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        // Save player data when they leave
        plugin.getDataManager().savePlayerData(event.getPlayer());
    }
}

