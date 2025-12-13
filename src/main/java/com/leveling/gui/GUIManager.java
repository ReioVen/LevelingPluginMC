package com.leveling.gui;

import com.leveling.LevelingPlugin;
import com.leveling.models.SkillType;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GUIManager implements Listener {
    private final LevelingPlugin plugin;
    private final Map<Player, Integer> leaderboardPages = new HashMap<>();
    private final Map<Player, SkillType> leaderboardSkills = new HashMap<>();
    
    public GUIManager(LevelingPlugin plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }
    
    /**
     * Create a black glass pane item for filling empty slots
     */
    public static ItemStack createBlackGlassPane() {
        ItemStack glass = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        ItemMeta meta = glass.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(" ");
            glass.setItemMeta(meta);
        }
        return glass;
    }
    
    /**
     * Fill empty slots in an inventory with black glass panes
     */
    public static void fillEmptySlots(Inventory inventory) {
        for (int i = 0; i < inventory.getSize(); i++) {
            if (inventory.getItem(i) == null || inventory.getItem(i).getType() == Material.AIR) {
                inventory.setItem(i, createBlackGlassPane());
            }
        }
    }
    
    /**
     * Create a named item with lore
     */
    public static ItemStack createItem(Material material, String name, String... lore) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(name);
            if (lore.length > 0) {
                List<String> loreList = new ArrayList<>();
                for (String line : lore) {
                    loreList.add(line);
                }
                meta.setLore(loreList);
            }
            item.setItemMeta(meta);
        }
        return item;
    }
    
    /**
     * Handle inventory clicks for GUI navigation
     */
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) {
            return;
        }
        
        Player player = (Player) event.getWhoClicked();
        String title = event.getView().getTitle();
        
        // Check if this is one of our custom GUIs
        boolean isOurGUI = title.contains("Level") || 
                          title.contains("Leaderboard") || 
                          title.contains("Stats") ||
                          title.contains("§6§l"); // Skill detail GUIs start with §6§l
        
        // Only handle clicks in our custom GUIs - ALWAYS cancel to prevent item removal
        if (isOurGUI) {
            // Cancel ALL clicks in our GUIs, regardless of who clicks (including OPs)
            event.setCancelled(true);
            
            // Also prevent taking items from the GUI
            if (event.getClickedInventory() != null && 
                event.getClickedInventory().equals(event.getView().getTopInventory())) {
                // Prevent all item movement from top inventory
                event.setCancelled(true);
            }
            
            ItemStack clicked = event.getCurrentItem();
            
            if (clicked == null || clicked.getType() == Material.AIR || clicked.getType() == Material.BLACK_STAINED_GLASS_PANE) {
                return;
            }
            
            // Handle All Skills GUI
            if (title.equals("§6§lYour Skill Levels")) {
                handleAllSkillsClick(player, clicked);
            }
            // Handle Skill Detail GUI - check if it's a skill detail (has icon and skill name, but not leaderboard)
            else if (title.contains("§6§l") && !title.contains("Leaderboard") && !title.contains("Your Skill Levels") && !title.contains("Statistics")) {
                handleSkillDetailClick(player, clicked, title);
            }
            // Handle Leaderboard GUI
            else if (title.contains("Leaderboard")) {
                handleLeaderboardClick(player, clicked, title);
            }
            // Handle Stats GUI
            else if (title.contains("Statistics")) {
                handleStatsClick(player, clicked);
            }
        }
    }
    
    private void handleAllSkillsClick(Player player, ItemStack clicked) {
        // Check if clicked on stats button
        if (clicked.getType() == Material.BOOK && clicked.getItemMeta() != null) {
            String displayName = clicked.getItemMeta().getDisplayName();
            if (displayName.contains("Statistics")) {
                new SkillStatsGUI(plugin, plugin.getSkillManager(), plugin.getExperienceManager()).openGUI(player);
                return;
            }
        }
        
        // Check if clicked on a skill item
        SkillType clickedSkill = getSkillFromItem(clicked);
        if (clickedSkill != null) {
            new SkillDetailGUI(plugin, plugin.getSkillManager(), plugin.getExperienceManager()).openGUI(player, clickedSkill);
        }
    }
    
    private void handleSkillDetailClick(Player player, ItemStack clicked, String title) {
        // Extract skill from title
        SkillType skill = extractSkillFromTitle(title);
        
        if (clicked == null || clicked.getItemMeta() == null) {
            return;
        }
        
        String displayName = clicked.getItemMeta().getDisplayName();
        
        // Handle Back button (ARROW)
        if (clicked.getType() == Material.ARROW) {
            if (displayName.contains("Back") || displayName.contains("←")) {
                new AllSkillsGUI(plugin, plugin.getSkillManager(), plugin.getExperienceManager()).openGUI(player);
                return;
            }
        }
        
        // Handle Leaderboard button (PLAYER_HEAD)
        if (clicked.getType() == Material.PLAYER_HEAD) {
            if (displayName.contains("Leaderboard") || displayName.contains("View Leaderboard")) {
                leaderboardSkills.put(player, skill);
                leaderboardPages.put(player, 1);
                new LeaderboardGUI(plugin, plugin.getSkillManager()).openGUI(player, skill, 1);
                return;
            }
        }
        
        // Handle Bonuses item (GOLDEN_APPLE)
        if (clicked.getType() == Material.GOLDEN_APPLE) {
            if (displayName.contains("Bonuses")) {
                // Just a display item, do nothing
                return;
            }
        }
        
        // Handle progress bar items (EXPERIENCE_BOTTLE, GLASS_BOTTLE)
        if (clicked.getType() == Material.EXPERIENCE_BOTTLE || clicked.getType() == Material.GLASS_BOTTLE) {
            if (displayName.contains("Progress")) {
                // Just a display item, do nothing
                return;
            }
        }
        
        // Handle skill item click (could be any skill material)
        SkillType clickedSkill = getSkillFromItem(clicked);
        if (clickedSkill != null && clickedSkill == skill) {
            // Clicked on the skill item - could refresh or do nothing
            // For now, do nothing as it's just a display item
        }
    }
    
    private void handleLeaderboardClick(Player player, ItemStack clicked, String title) {
        SkillType skill = extractSkillFromTitle(title);
        leaderboardSkills.put(player, skill);
        int currentPage = leaderboardPages.getOrDefault(player, 1);
        
        if (clicked == null || clicked.getItemMeta() == null) {
            return;
        }
        
        String displayName = clicked.getItemMeta().getDisplayName();
        
        if (clicked.getType() == Material.ARROW) {
            if (displayName.contains("Previous")) {
                int newPage = Math.max(1, currentPage - 1);
                new LeaderboardGUI(plugin, plugin.getSkillManager()).openGUI(player, skill, newPage);
                leaderboardPages.put(player, newPage);
            } else if (displayName.contains("Next")) {
                int newPage = currentPage + 1;
                new LeaderboardGUI(plugin, plugin.getSkillManager()).openGUI(player, skill, newPage);
                leaderboardPages.put(player, newPage);
            }
        } else if (clicked.getType() == Material.BARRIER) {
            player.closeInventory();
            leaderboardPages.remove(player);
            leaderboardSkills.remove(player);
        }
    }
    
    private void handleStatsClick(Player player, ItemStack clicked) {
        // Stats GUI - clicking on skills could open detail view
        SkillType clickedSkill = getSkillFromItem(clicked);
        if (clickedSkill != null) {
            new SkillDetailGUI(plugin, plugin.getSkillManager(), plugin.getExperienceManager()).openGUI(player, clickedSkill);
        }
    }
    
    private SkillType getSkillFromItem(ItemStack item) {
        if (item == null || item.getItemMeta() == null) {
            return null;
        }
        
        String displayName = item.getItemMeta().getDisplayName();
        for (SkillType skill : SkillType.values()) {
            if (displayName.contains(skill.getDisplayName()) || displayName.contains(skill.getIcon())) {
                return skill;
            }
        }
        return null;
    }
    
    private SkillType extractSkillFromTitle(String title) {
        for (SkillType skill : SkillType.values()) {
            if (title.contains(skill.getDisplayName()) || title.contains(skill.getIcon())) {
                return skill;
            }
        }
        return SkillType.MINING;
    }
    
    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        // Prevent dragging items in our custom GUIs - applies to everyone including OPs
        String title = event.getView().getTitle();
        boolean isOurGUI = title.contains("Level") || 
                          title.contains("Leaderboard") || 
                          title.contains("Stats") ||
                          title.contains("§6§l"); // Skill detail GUIs start with §6§l
        if (isOurGUI) {
            event.setCancelled(true);
        }
    }
    
    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (event.getPlayer() instanceof Player) {
            Player player = (Player) event.getPlayer();
            // Clean up tracking when inventory closes
            leaderboardPages.remove(player);
            leaderboardSkills.remove(player);
        }
    }
}


