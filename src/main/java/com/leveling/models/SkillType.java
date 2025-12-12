package com.leveling.models;

public enum SkillType {
    MINING("Mining", "⛏"),
    EXCAVATION("Excavation", "⛏"),
    HERBALISM("Herbalism", "🌿"),
    WOODCUTTING("Woodcutting", "🪓"),
    COMBAT("Combat", "⚔"),
    DEFENSE("Defense", "🛡"),
    FARMING("Farming", "🌾"),
    SMITHING("Smithing", "🔨"),
    ACROBATICS("Acrobatics", "🤸");
    
    private final String displayName;
    private final String icon;
    
    SkillType(String displayName, String icon) {
        this.displayName = displayName;
        this.icon = icon;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public String getIcon() {
        return icon;
    }
}

