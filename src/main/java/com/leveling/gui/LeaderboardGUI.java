package com.leveling.gui;

import com.leveling.LevelingPlugin;
import com.leveling.managers.SkillManager;
import com.leveling.models.PlayerSkillData;
import com.leveling.models.SkillType;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class LeaderboardGUI {
    private final LevelingPlugin plugin;
    private final SkillManager skillManager;
    
    public LeaderboardGUI(LevelingPlugin plugin, SkillManager skillManager) {
        this.plugin = plugin;
        this.skillManager = skillManager;
    }
    
    public void openGUI(Player player, SkillType skill, int page) {
        // Get leaderboard data
        Map<UUID, PlayerSkillData> allData = skillManager.getAllPlayerData();
        List<Map.Entry<UUID, Integer>> leaderboard = new ArrayList<>();
        
        for (Map.Entry<UUID, PlayerSkillData> entry : allData.entrySet()) {
            int level = entry.getValue().getLevel(skill);
            if (level > 1) {
                leaderboard.add(new AbstractMap.SimpleEntry<>(entry.getKey(), level));
            }
        }
        
        leaderboard.sort((a, b) -> Integer.compare(b.getValue(), a.getValue()));
        
        int pageSize = 21; // 3 rows of 7 slots
        int totalPages = Math.max(1, (int) Math.ceil((double) leaderboard.size() / pageSize));
        page = Math.max(1, Math.min(page, totalPages));
        
        Inventory gui = plugin.getServer().createInventory(null, 54, 
            "§6§l" + skill.getIcon() + " " + skill.getDisplayName() + " Leaderboard");
        
        // Title item
        ItemStack titleItem = GUIManager.createItem(
            Material.GOLDEN_HELMET,
            "§6§l" + skill.getIcon() + " " + skill.getDisplayName() + " Leaderboard",
            "§7Page §e" + page + "§7/§e" + totalPages
        );
        gui.setItem(4, titleItem);
        
        // Leaderboard entries (slots 9-35, skipping borders)
        int start = (page - 1) * pageSize;
        int end = Math.min(start + pageSize, leaderboard.size());
        
        int[] leaderboardSlots = {
            9, 10, 11, 12, 13, 14, 15,
            18, 19, 20, 21, 22, 23, 24,
            27, 28, 29, 30, 31, 32, 33
        };
        
        for (int i = start; i < end; i++) {
            Map.Entry<UUID, Integer> entry = leaderboard.get(i);
            UUID uuid = entry.getKey();
            int level = entry.getValue();
            int rank = i + 1;
            
            int slotIndex = i - start;
            if (slotIndex < leaderboardSlots.length) {
                ItemStack head = createPlayerHead(uuid, rank, level, skill);
                gui.setItem(leaderboardSlots[slotIndex], head);
            }
        }
        
        // Navigation buttons
        if (page > 1) {
            ItemStack prevItem = GUIManager.createItem(
                Material.ARROW,
                "§7§l← Previous Page",
                "§7Click to go to page " + (page - 1)
            );
            gui.setItem(45, prevItem);
        }
        
        if (page < totalPages) {
            ItemStack nextItem = GUIManager.createItem(
                Material.ARROW,
                "§7§lNext Page →",
                "§7Click to go to page " + (page + 1)
            );
            gui.setItem(53, nextItem);
        }
        
        // Back button
        ItemStack backItem = GUIManager.createItem(
            Material.BARRIER,
            "§c§lClose",
            "§7Click to close this menu"
        );
        gui.setItem(49, backItem);
        
        // Fill empty slots
        GUIManager.fillEmptySlots(gui);
        
        player.openInventory(gui);
    }
    
    private ItemStack createPlayerHead(UUID uuid, int rank, int level, SkillType skill) {
        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) head.getItemMeta();
        
        if (meta != null) {
            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);
            String playerName = offlinePlayer.getName();
            if (playerName == null) {
                playerName = "Unknown";
            }
            
            // Set player head
            if (offlinePlayer.hasPlayedBefore() || offlinePlayer.isOnline()) {
                meta.setOwningPlayer(offlinePlayer);
            }
            
            // Set display name with rank color
            String rankColor = getRankColor(rank);
            meta.setDisplayName(rankColor + "#" + rank + " " + playerName);
            
            // Set lore
            List<String> lore = new ArrayList<>();
            lore.add("§7");
            lore.add("§7Level: §e" + level);
            lore.add("§7Skill: §6" + skill.getIcon() + " " + skill.getDisplayName());
            lore.add("§7");
            lore.add("§7Rank: §e#" + rank);
            
            meta.setLore(lore);
            head.setItemMeta(meta);
        }
        
        return head;
    }
    
    private String getRankColor(int rank) {
        if (rank == 1) return "§6§l"; // Gold for #1
        if (rank == 2) return "§7§l"; // Silver for #2
        if (rank == 3) return "§c§l"; // Bronze for #3
        return "§f"; // White for others
    }
}





