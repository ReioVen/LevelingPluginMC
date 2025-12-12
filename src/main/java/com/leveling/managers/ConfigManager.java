package com.leveling.managers;

import com.leveling.LevelingPlugin;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.HashMap;
import java.util.Map;

public class ConfigManager {
    private final LevelingPlugin plugin;
    private final FileConfiguration config;
    
    private int maxLevel;
    private double baseExp;
    private double expMultiplier;
    private Map<Material, Integer> miningExp;
    private Map<Material, Integer> excavationExp;
    private Map<Material, Integer> herbalismExp;
    private Map<Material, Integer> woodcuttingExp;
    private double combatExpPlayer;
    private double combatExpMob;
    private double defenseExpDamage;
    private double defenseExpKill;
    private double farmingExpBreed;
    private double farmingExpHarvest;
    private double smithingExpCraft;
    private double acrobaticsExpFall;
    
    // Skill bonuses
    private double miningDoubleDropChancePerLevel; // 2% per level
    private double excavationDoubleDropChancePerLevel; // 1% per level
    private double herbalismDoubleDropChancePerLevel; // 2% per level
    private int herbalismAutoReplantLevel; // Level 30
    private double combatDamagePerLevel; // 0.5% per level
    private double combatDamageMaxPercent; // Max 25%
    private double defenseToughnessPerLevel; // 0.5% per level
    private double defenseToughnessMaxPercent; // Max 25%
    private double acrobaticsDamageReductionPerLevel; // 1% per level
    private double smithingResourceReturnChance; // 0.2% chance
    
    // Excavation rare drops
    private double excavationDiamondChance10; // 1% at level 10
    private double excavationDiamondChance20; // 2% at level 20
    private double excavationDiamondChance30; // 3% at level 30
    private double excavationDiamondChance40; // 5% at level 40
    private double excavationDiamondChance50; // 7% at level 50
    private double excavationGoldChance40; // 2% at level 40+
    private double excavationNetheriteChance50; // 3% at level 50
    
    public ConfigManager(LevelingPlugin plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfig();
        loadConfig();
    }
    
    private void loadConfig() {
        maxLevel = config.getInt("max-level", 50);
        baseExp = config.getDouble("experience.base", 100.0);
        expMultiplier = config.getDouble("experience.multiplier", 1.5);
        
        // Load mining experience
        miningExp = new HashMap<>();
        var miningSection = config.getConfigurationSection("experience-gain.mining");
        if (miningSection != null) {
            for (String key : miningSection.getKeys(false)) {
                try {
                    Material mat = Material.valueOf(key.toUpperCase());
                    miningExp.put(mat, miningSection.getInt(key));
                } catch (IllegalArgumentException e) {
                    plugin.getLogger().warning("Invalid material in mining config: " + key);
                }
            }
        }
        
        // Load herbalism experience
        herbalismExp = new HashMap<>();
        var herbalismSection = config.getConfigurationSection("experience-gain.herbalism");
        if (herbalismSection != null) {
            for (String key : herbalismSection.getKeys(false)) {
                try {
                    Material mat = Material.valueOf(key.toUpperCase());
                    herbalismExp.put(mat, herbalismSection.getInt(key));
                } catch (IllegalArgumentException e) {
                    plugin.getLogger().warning("Invalid material in herbalism config: " + key);
                }
            }
        }
        
        // Load woodcutting experience
        woodcuttingExp = new HashMap<>();
        var woodcuttingSection = config.getConfigurationSection("experience-gain.woodcutting");
        if (woodcuttingSection != null) {
            for (String key : woodcuttingSection.getKeys(false)) {
                try {
                    Material mat = Material.valueOf(key.toUpperCase() + "_LOG");
                    woodcuttingExp.put(mat, woodcuttingSection.getInt(key));
                } catch (IllegalArgumentException e) {
                    try {
                        Material mat = Material.valueOf(key.toUpperCase() + "_STEM");
                        woodcuttingExp.put(mat, woodcuttingSection.getInt(key));
                    } catch (IllegalArgumentException e2) {
                        plugin.getLogger().warning("Invalid material in woodcutting config: " + key);
                    }
                }
            }
        }
        
        // Load excavation experience
        excavationExp = new HashMap<>();
        var excavationSection = config.getConfigurationSection("experience-gain.excavation");
        if (excavationSection != null) {
            for (String key : excavationSection.getKeys(false)) {
                try {
                    Material mat = Material.valueOf(key.toUpperCase());
                    excavationExp.put(mat, excavationSection.getInt(key));
                } catch (IllegalArgumentException e) {
                    plugin.getLogger().warning("Invalid material in excavation config: " + key);
                }
            }
        }
        combatExpPlayer = config.getDouble("experience-gain.combat.player", 20.0);
        combatExpMob = config.getDouble("experience-gain.combat.mob", 10.0);
        defenseExpDamage = config.getDouble("experience-gain.defense.damage-taken", 2.0);
        defenseExpKill = config.getDouble("experience-gain.defense.player-kill", 15.0);
        farmingExpBreed = config.getDouble("experience-gain.farming.animal-breed", 8.0);
        farmingExpHarvest = config.getDouble("experience-gain.farming.crop-harvest", 4.0);
        smithingExpCraft = config.getDouble("experience-gain.smithing.item-craft", 5.0);
        acrobaticsExpFall = config.getDouble("experience-gain.acrobatics.fall-damage", 3.0);
        
        // Skill bonuses
        miningDoubleDropChancePerLevel = config.getDouble("skill-bonuses.mining.double-drop-chance-per-level", 0.02); // 2% per level
        excavationDoubleDropChancePerLevel = config.getDouble("skill-bonuses.excavation.double-drop-chance-per-level", 0.01); // 1% per level
        herbalismDoubleDropChancePerLevel = config.getDouble("skill-bonuses.herbalism.double-drop-chance-per-level", 0.02); // 2% per level
        herbalismAutoReplantLevel = config.getInt("skill-bonuses.herbalism.auto-replant-level", 30);
        
        // Combat bonuses
        combatDamagePerLevel = config.getDouble("skill-bonuses.combat.damage-per-level", 0.005); // 0.5% per level
        combatDamageMaxPercent = config.getDouble("skill-bonuses.combat.max-damage-percent", 0.25); // Max 25%
        
        // Defense bonuses
        defenseToughnessPerLevel = config.getDouble("skill-bonuses.defense.toughness-per-level", 0.005); // 0.5% per level
        defenseToughnessMaxPercent = config.getDouble("skill-bonuses.defense.max-toughness-percent", 0.25); // Max 25%
        
        // Acrobatics bonuses
        acrobaticsDamageReductionPerLevel = config.getDouble("skill-bonuses.acrobatics.damage-reduction-per-level", 0.01); // 1% per level
        
        // Smithing bonuses
        smithingResourceReturnChance = config.getDouble("skill-bonuses.smithing.resource-return-chance", 0.002); // 0.2% chance
        
        // Excavation rare drops
        excavationDiamondChance10 = config.getDouble("skill-bonuses.excavation.rare-drops.diamond.level-10", 0.01); // 1%
        excavationDiamondChance20 = config.getDouble("skill-bonuses.excavation.rare-drops.diamond.level-20", 0.02); // 2%
        excavationDiamondChance30 = config.getDouble("skill-bonuses.excavation.rare-drops.diamond.level-30", 0.03); // 3%
        excavationDiamondChance40 = config.getDouble("skill-bonuses.excavation.rare-drops.diamond.level-40", 0.05); // 5%
        excavationDiamondChance50 = config.getDouble("skill-bonuses.excavation.rare-drops.diamond.level-50", 0.07); // 7%
        excavationGoldChance40 = config.getDouble("skill-bonuses.excavation.rare-drops.gold.level-40", 0.02); // 2%
        excavationNetheriteChance50 = config.getDouble("skill-bonuses.excavation.rare-drops.netherite.level-50", 0.03); // 3%
    }
    
    public int getMaxLevel() {
        return maxLevel;
    }
    
    public double getBaseExp() {
        return baseExp;
    }
    
    public double getExpMultiplier() {
        return expMultiplier;
    }
    
    public int getMiningExp(Material material) {
        return miningExp.getOrDefault(material, 0);
    }
    
    public int getHerbalismExp(Material material) {
        return herbalismExp.getOrDefault(material, 0);
    }
    
    public int getWoodcuttingExp(Material material) {
        return woodcuttingExp.getOrDefault(material, 0);
    }
    
    public int getExcavationExp(Material material) {
        return excavationExp.getOrDefault(material, 0);
    }
    
    public double getCombatExpPlayer() {
        return combatExpPlayer;
    }
    
    public double getCombatExpMob() {
        return combatExpMob;
    }
    
    public double getDefenseExpDamage() {
        return defenseExpDamage;
    }
    
    public double getDefenseExpKill() {
        return defenseExpKill;
    }
    
    public double getFarmingExpBreed() {
        return farmingExpBreed;
    }
    
    public double getFarmingExpHarvest() {
        return farmingExpHarvest;
    }
    
    public double getSmithingExpCraft() {
        return smithingExpCraft;
    }
    
    public double getAcrobaticsExpFall() {
        return acrobaticsExpFall;
    }
    
    // Skill bonus getters
    public double getMiningDoubleDropChancePerLevel() {
        return miningDoubleDropChancePerLevel;
    }
    
    public double getExcavationDoubleDropChancePerLevel() {
        return excavationDoubleDropChancePerLevel;
    }
    
    public double getHerbalismDoubleDropChancePerLevel() {
        return herbalismDoubleDropChancePerLevel;
    }
    
    public int getHerbalismAutoReplantLevel() {
        return herbalismAutoReplantLevel;
    }
    
    public double getCombatDamagePerLevel() {
        return combatDamagePerLevel;
    }
    
    public double getCombatDamageMaxPercent() {
        return combatDamageMaxPercent;
    }
    
    public double getDefenseToughnessPerLevel() {
        return defenseToughnessPerLevel;
    }
    
    public double getDefenseToughnessMaxPercent() {
        return defenseToughnessMaxPercent;
    }
    
    public double getAcrobaticsDamageReductionPerLevel() {
        return acrobaticsDamageReductionPerLevel;
    }
    
    public double getSmithingResourceReturnChance() {
        return smithingResourceReturnChance;
    }
    
    // Excavation rare drop getters
    public double getExcavationDiamondChance10() {
        return excavationDiamondChance10;
    }
    
    public double getExcavationDiamondChance20() {
        return excavationDiamondChance20;
    }
    
    public double getExcavationDiamondChance30() {
        return excavationDiamondChance30;
    }
    
    public double getExcavationDiamondChance40() {
        return excavationDiamondChance40;
    }
    
    public double getExcavationDiamondChance50() {
        return excavationDiamondChance50;
    }
    
    public double getExcavationGoldChance40() {
        return excavationGoldChance40;
    }
    
    public double getExcavationNetheriteChance50() {
        return excavationNetheriteChance50;
    }
}

