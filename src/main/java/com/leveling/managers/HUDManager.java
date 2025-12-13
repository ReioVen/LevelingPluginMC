package com.leveling.managers;

import com.leveling.LevelingPlugin;
import com.leveling.models.SkillType;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class HUDManager {
    private final LevelingPlugin plugin;
    private final SkillManager skillManager;
    private final ExperienceManager experienceManager;
    private BukkitTask hudTask;
    private final Map<UUID, Long> lastActivityTime;
    private final Map<UUID, SkillType> lastActiveSkill;
    private final long displayDuration;
    
    public HUDManager(LevelingPlugin plugin, SkillManager skillManager, ExperienceManager experienceManager) {
        this.plugin = plugin;
        this.skillManager = skillManager;
        this.experienceManager = experienceManager;
        this.lastActivityTime = new HashMap<>();
        this.lastActiveSkill = new HashMap<>();
        // Convert ticks to milliseconds (1 tick = 50ms)
        long ticks = plugin.getConfig().getLong("hud.display-duration", 60);
        this.displayDuration = ticks * 50;
    }
    
    public void startHUDTask() {
        if (!plugin.getConfig().getBoolean("hud.enabled", true)) {
            return;
        }
        
        int interval = plugin.getConfig().getInt("hud.update-interval", 20);
        
        hudTask = new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : plugin.getServer().getOnlinePlayers()) {
                    updateHUD(player);
                }
            }
        }.runTaskTimer(plugin, 0L, interval);
    }
    
    public void stopHUDTask() {
        if (hudTask != null) {
            hudTask.cancel();
        }
    }
    
    public void showSkillProgress(Player player, SkillType skill) {
        lastActiveSkill.put(player.getUniqueId(), skill);
        lastActivityTime.put(player.getUniqueId(), System.currentTimeMillis());
        updateHUD(player);
    }
    
    private void updateHUD(Player player) {
        UUID uuid = player.getUniqueId();
        
        // Check if we should show HUD
        if (!lastActivityTime.containsKey(uuid)) {
            return;
        }
        
        long timeSinceActivity = System.currentTimeMillis() - lastActivityTime.get(uuid);
        if (timeSinceActivity > displayDuration) {
            // Hide HUD after duration
            lastActivityTime.remove(uuid);
            lastActiveSkill.remove(uuid);
            return;
        }
        
        SkillType skill = lastActiveSkill.get(uuid);
        if (skill == null) {
            return;
        }
        
        int level = skillManager.getLevel(player, skill);
        double progress = experienceManager.getProgressPercentage(player, skill);
        double totalExp = experienceManager.getTotalExperience(player, skill);
        double expForNext = experienceManager.getExperienceForNextLevel(player, skill);
        
        // Format progress bar
        int barLength = 20;
        int filled = (int) (barLength * (progress / 100.0));
        StringBuilder progressBar = new StringBuilder("§7[");
        
        for (int i = 0; i < barLength; i++) {
            if (i < filled) {
                progressBar.append("§a█");
            } else {
                progressBar.append("§8█");
            }
        }
        progressBar.append("§7]");
        
        // Create HUD message - show total XP and percentage
        String message = String.format(
            "§6%s %s §7Level §e%d §7(§b%.1f%%§7) §8| §7Exp: §b%.0f§7/§b%.0f",
            skill.getIcon(),
            skill.getDisplayName(),
            level,
            progress,
            totalExp,
            expForNext
        );
        
        // Send action bar message (appears above hotbar) - using Paper's Adventure API
        // Convert legacy color codes to Adventure Component
        Component component = LegacyComponentSerializer.legacySection().deserialize(message);
        player.sendActionBar(component);
    }
}

