package com.leveling.listeners;

import com.leveling.LevelingPlugin;
import com.leveling.managers.ExperienceManager;
import com.leveling.managers.SkillManager;
import com.leveling.models.SkillType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;

public class DefenseListener implements Listener {
    private final LevelingPlugin plugin;
    private final ExperienceManager experienceManager;
    private final SkillManager skillManager;
    
    public DefenseListener(LevelingPlugin plugin, ExperienceManager experienceManager, SkillManager skillManager) {
        this.plugin = plugin;
        this.experienceManager = experienceManager;
        this.skillManager = skillManager;
    }
    
    @EventHandler
    public void onPlayerDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }
        
        Player player = (Player) event.getEntity();
        
        // Don't give defense XP for fall damage (that's for acrobatics skill)
        if (event.getCause() == EntityDamageEvent.DamageCause.FALL) {
            return;
        }
        
        // Only give XP if player actually takes damage (not blocked by plugins)
        // Check if the damage was cancelled or if final damage is 0
        if (event.isCancelled() || event.getFinalDamage() <= 0) {
            return;
        }
        
        // Get the player's health before damage
        double healthBefore = player.getHealth();
        
        // Schedule check after damage is applied to verify actual damage taken
        plugin.getServer().getScheduler().runTask(plugin, () -> {
            double healthAfter = player.getHealth();
            double actualDamage = healthBefore - healthAfter;
            
            // Only give XP if player actually took damage (not blocked by friendly fire protection, etc.)
            if (actualDamage > 0) {
                double exp = actualDamage * plugin.getConfigManager().getDefenseExpDamage();
                experienceManager.addExperience(player, SkillType.DEFENSE, exp);
                plugin.getHUDManager().showSkillProgress(player, SkillType.DEFENSE);
                
                // Update defense and combat bonuses
                skillManager.updateDefenseBonuses(player);
                skillManager.updateCombatBonuses(player);
            }
        });
    }
    
    @EventHandler
    public void onPlayerKill(PlayerDeathEvent event) {
        Player victim = event.getEntity();
        Player killer = victim.getKiller();
        
        if (killer == null || killer == victim) {
            return;
        }
        
        // Give defense experience for PvP kill
        double exp = plugin.getConfigManager().getDefenseExpKill();
        experienceManager.addExperience(killer, SkillType.DEFENSE, exp);
        plugin.getHUDManager().showSkillProgress(killer, SkillType.DEFENSE);
        
        // Update defense bonuses
        skillManager.updateDefenseBonuses(killer);
    }
}

