package com.leveling.listeners;

import com.leveling.LevelingPlugin;
import com.leveling.managers.ExperienceManager;
import com.leveling.models.SkillType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

public class AcrobaticsListener implements Listener {
    private final LevelingPlugin plugin;
    private final ExperienceManager experienceManager;
    
    public AcrobaticsListener(LevelingPlugin plugin, ExperienceManager experienceManager) {
        this.plugin = plugin;
        this.experienceManager = experienceManager;
    }
    
    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }
        
        Player player = (Player) event.getEntity();
        
        // Give acrobatics experience for fall damage (can be reduced by skill level)
        if (event.getCause() == EntityDamageEvent.DamageCause.FALL) {
            double exp = plugin.getConfigManager().getAcrobaticsExpFall();
            experienceManager.addExperience(player, SkillType.ACROBATICS, exp);
            plugin.getHUDManager().showSkillProgress(player, SkillType.ACROBATICS);
            
            // Reduce fall damage by configured percentage per level
            int acrobaticsLevel = plugin.getSkillManager().getLevel(player, SkillType.ACROBATICS);
            double damageReductionPercent = acrobaticsLevel * plugin.getConfigManager().getAcrobaticsDamageReductionPerLevel();
            double reduction = event.getDamage() * damageReductionPercent;
            double newDamage = Math.max(0, event.getDamage() - reduction);
            event.setDamage(newDamage);
        }
    }
}

