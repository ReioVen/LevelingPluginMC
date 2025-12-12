package com.leveling.listeners;

import com.leveling.LevelingPlugin;
import com.leveling.managers.ExperienceManager;
import com.leveling.models.SkillType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

public class CombatListener implements Listener {
    private final LevelingPlugin plugin;
    private final ExperienceManager experienceManager;
    
    public CombatListener(LevelingPlugin plugin, ExperienceManager experienceManager) {
        this.plugin = plugin;
        this.experienceManager = experienceManager;
    }
    
    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        LivingEntity entity = event.getEntity();
        Player killer = entity.getKiller();
        
        if (killer == null) {
            return;
        }
        
        double exp;
        if (entity instanceof Player) {
            exp = plugin.getConfigManager().getCombatExpPlayer();
        } else {
            exp = plugin.getConfigManager().getCombatExpMob();
        }
        
        experienceManager.addExperience(killer, SkillType.COMBAT, exp);
        plugin.getHUDManager().showSkillProgress(killer, SkillType.COMBAT);
    }
}

