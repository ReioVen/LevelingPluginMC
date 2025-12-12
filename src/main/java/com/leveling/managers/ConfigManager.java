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
    private Map<Material, Integer> farmingExp;
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
    private double farmingDoubleDropChancePerLevel; // 2% per level
    private int farmingAutoReplantLevel; // Level 30
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
                    Material mat;
                    String upperKey = key.toUpperCase();
                    
                    // Handle ore names - try with _ORE suffix first, then without
                    if (upperKey.equals("STONE")) {
                        mat = Material.STONE;
                    } else if (upperKey.equals("COBBLESTONE")) {
                        mat = Material.COBBLESTONE;
                    } else if (upperKey.equals("DEEPSLATE")) {
                        mat = Material.DEEPSLATE;
                    } else if (upperKey.equals("COBBLED_DEEPSLATE")) {
                        mat = Material.COBBLED_DEEPSLATE;
                    } else {
                        // Try with _ORE suffix first (for ores)
                        try {
                            mat = Material.valueOf(upperKey + "_ORE");
                        } catch (IllegalArgumentException e1) {
                            // Try without _ORE (for other blocks)
                            try {
                                mat = Material.valueOf(upperKey);
                            } catch (IllegalArgumentException e2) {
                                // Try DEEPSLATE variant
                                try {
                                    mat = Material.valueOf("DEEPSLATE_" + upperKey + "_ORE");
                                } catch (IllegalArgumentException e3) {
                                    throw new IllegalArgumentException("Material not found: " + key);
                                }
                            }
                        }
                    }
                    
                    miningExp.put(mat, miningSection.getInt(key));
                    
                    // Also add deepslate variants for ores
                    if (mat.name().contains("_ORE") && !mat.name().contains("DEEPSLATE")) {
                        try {
                            Material deepslateMat = Material.valueOf("DEEPSLATE_" + mat.name());
                            miningExp.put(deepslateMat, miningSection.getInt(key));
                        } catch (IllegalArgumentException e) {
                            // Deepslate variant doesn't exist, skip
                        }
                    }
                } catch (IllegalArgumentException e) {
                    plugin.getLogger().warning("Invalid material in mining config: " + key + " - " + e.getMessage());
                }
            }
        }
        
        // Load farming experience (merged from herbalism and farming)
        farmingExp = new HashMap<>();
        
        // Load from farming section
        var farmingSection = config.getConfigurationSection("experience-gain.farming");
        if (farmingSection != null) {
            for (String key : farmingSection.getKeys(false)) {
                if (key.equals("animal-breed") || key.equals("crop-harvest")) {
                    continue; // Skip these, they're handled separately
                }
                try {
                    Material mat;
                    String upperKey = key.toUpperCase();
                    
                    // Handle special material name mappings
                    switch (upperKey) {
                        case "SUGARCANE":
                            mat = Material.SUGAR_CANE;
                            break;
                        case "MELON":
                            mat = Material.MELON;
                            break;
                        case "PUMPKIN":
                            mat = Material.PUMPKIN;
                            break;
                        case "CACTUS":
                            mat = Material.CACTUS;
                            break;
                        case "CHORUS_FRUIT":
                            mat = Material.CHORUS_PLANT; // The block, not the fruit item
                            break;
                        case "COCOA":
                            mat = Material.COCOA;
                            break;
                        case "SWEET_BERRIES":
                            mat = Material.SWEET_BERRY_BUSH;
                            break;
                        case "GLOW_BERRIES":
                            mat = Material.CAVE_VINES;
                            break;
                        default:
                            // Try direct match first
                            try {
                                mat = Material.valueOf(upperKey);
                            } catch (IllegalArgumentException e1) {
                                // Try with _BLOCK suffix
                                try {
                                    mat = Material.valueOf(upperKey + "_BLOCK");
                                } catch (IllegalArgumentException e2) {
                                    throw new IllegalArgumentException("Material not found: " + key);
                                }
                            }
                            break;
                    }
                    
                    farmingExp.put(mat, farmingSection.getInt(key));
                } catch (IllegalArgumentException e) {
                    plugin.getLogger().warning("Invalid material in farming config: " + key + " - " + e.getMessage());
                }
            }
        }
        
        // Also load from herbalism section (for backwards compatibility)
        var herbalismSection = config.getConfigurationSection("experience-gain.herbalism");
        if (herbalismSection != null) {
            for (String key : herbalismSection.getKeys(false)) {
                try {
                    Material mat = Material.valueOf(key.toUpperCase());
                    // Only add if not already in farmingExp (farming takes priority)
                    if (!farmingExp.containsKey(mat)) {
                        farmingExp.put(mat, herbalismSection.getInt(key));
                    }
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
        farmingDoubleDropChancePerLevel = config.getDouble("skill-bonuses.farming.double-drop-chance-per-level", 0.02); // 2% per level
        farmingAutoReplantLevel = config.getInt("skill-bonuses.farming.auto-replant-level", 30);
        
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
    
    public int getFarmingExp(Material material) {
        return farmingExp.getOrDefault(material, 0);
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
    
    public double getFarmingDoubleDropChancePerLevel() {
        return farmingDoubleDropChancePerLevel;
    }
    
    public int getFarmingAutoReplantLevel() {
        return farmingAutoReplantLevel;
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

