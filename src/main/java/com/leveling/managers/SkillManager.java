package com.leveling.managers;

import com.leveling.LevelingPlugin;
import com.leveling.models.PlayerSkillData;
import com.leveling.models.SkillType;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SkillManager {
    private final LevelingPlugin plugin;
    private final Map<UUID, PlayerSkillData> playerData;
    private static final String DEFENSE_TOUGHNESS_MODIFIER = "leveling_defense_toughness";
    private static final String COMBAT_DAMAGE_MODIFIER = "leveling_combat_damage";
    
    public SkillManager(LevelingPlugin plugin) {
        this.plugin = plugin;
        this.playerData = new HashMap<>();
    }
    
    public PlayerSkillData getPlayerData(UUID uuid) {
        return playerData.computeIfAbsent(uuid, PlayerSkillData::new);
    }
    
    public PlayerSkillData getPlayerData(Player player) {
        return getPlayerData(player.getUniqueId());
    }
    
    public void setPlayerData(UUID uuid, PlayerSkillData data) {
        playerData.put(uuid, data);
    }
    
    public int getLevel(Player player, SkillType skill) {
        return getPlayerData(player).getLevel(skill);
    }
    
    public void setLevel(Player player, SkillType skill, int level) {
        int maxLevel = plugin.getConfigManager().getMaxLevel();
        level = Math.max(1, Math.min(level, maxLevel));
        getPlayerData(player).setLevel(skill, level);
    }
    
    public double getExperience(Player player, SkillType skill) {
        return getPlayerData(player).getExperience(skill);
    }
    
    public void updateDefenseBonuses(Player player) {
        int defenseLevel = getLevel(player, SkillType.DEFENSE);
        ConfigManager config = plugin.getConfigManager();
        
        // Calculate toughness bonus (configurable)
        double toughnessPerLevel = config.getDefenseToughnessPerLevel();
        double maxToughness = config.getDefenseToughnessMaxPercent();
        double toughnessPercent = Math.min(maxToughness, defenseLevel * toughnessPerLevel);
        
        // Remove old modifiers
        removeAttributeModifier(player, Attribute.GENERIC_ARMOR_TOUGHNESS, DEFENSE_TOUGHNESS_MODIFIER);
        
        // Apply toughness bonus (percentage-based using MULTIPLY_SCALAR_1)
        if (toughnessPercent > 0) {
            AttributeModifier toughnessMod = new AttributeModifier(
                UUID.nameUUIDFromBytes(DEFENSE_TOUGHNESS_MODIFIER.getBytes()),
                DEFENSE_TOUGHNESS_MODIFIER,
                toughnessPercent,
                AttributeModifier.Operation.MULTIPLY_SCALAR_1
            );
            player.getAttribute(Attribute.GENERIC_ARMOR_TOUGHNESS).addModifier(toughnessMod);
        }
    }
    
    public void updateCombatBonuses(Player player) {
        int combatLevel = getLevel(player, SkillType.COMBAT);
        ConfigManager config = plugin.getConfigManager();
        
        // Calculate damage bonus (configurable)
        double damagePerLevel = config.getCombatDamagePerLevel();
        double maxDamage = config.getCombatDamageMaxPercent();
        double damagePercent = Math.min(maxDamage, combatLevel * damagePerLevel);
        
        // Remove old modifiers
        removeAttributeModifier(player, Attribute.GENERIC_ATTACK_DAMAGE, COMBAT_DAMAGE_MODIFIER);
        
        // Apply damage bonus (percentage-based using MULTIPLY_SCALAR_1)
        if (damagePercent > 0) {
            AttributeModifier damageMod = new AttributeModifier(
                UUID.nameUUIDFromBytes(COMBAT_DAMAGE_MODIFIER.getBytes()),
                COMBAT_DAMAGE_MODIFIER,
                damagePercent,
                AttributeModifier.Operation.MULTIPLY_SCALAR_1
            );
            player.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).addModifier(damageMod);
        }
    }
    
    private void removeAttributeModifier(Player player, Attribute attribute, String modifierName) {
        if (player.getAttribute(attribute) != null) {
            UUID modifierUUID = UUID.nameUUIDFromBytes(modifierName.getBytes());
            player.getAttribute(attribute).getModifiers().stream()
                .filter(mod -> mod.getUniqueId().equals(modifierUUID))
                .forEach(mod -> player.getAttribute(attribute).removeModifier(mod));
        }
    }
    
    public Map<UUID, PlayerSkillData> getAllPlayerData() {
        return new HashMap<>(playerData);
    }
    
    public void removePlayerData(UUID uuid) {
        playerData.remove(uuid);
    }
}

