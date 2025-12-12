# LevelingPlugin Testing Guide

## ✅ Build Status
The plugin has been successfully built! The JAR file is located at:
- `target/LevelingPlugin-1.0.0.jar`

## 🚀 Deployment Steps

### 1. Copy to Server
Copy the JAR file to your Minecraft server's `plugins/` folder:
```
[Your Server]/plugins/LevelingPlugin-1.0.0.jar
```

### 2. Start/Restart Server
- If server is running: `/reload` or restart the server
- If server is off: Start it normally

### 3. Verify Plugin Loaded
Check server console for:
```
[LevelingPlugin] LevelingPlugin has been enabled!
```

## 🧪 Testing Checklist

### Basic Functionality Tests

#### 1. **Commands Test**
- `/skills` - Should show all your skill levels
- `/skills MINING` - Should show detailed mining skill info
- `/skillstats` - Should show all detailed statistics

#### 2. **Mining Skill**
- Break stone, coal, iron, gold, diamond, emerald, or netherite
- **Expected:** HUD appears above hotbar showing:
  - ⛏ Mining icon
  - Level number
  - Progress percentage
  - Experience gained
- **Expected:** Experience increases and levels up

#### 3. **Herbalism Skill**
- Break wheat, carrots, potatoes, beetroot, pumpkins, melons, sugarcane, cactus, nether wart, or chorus fruit
- **Expected:** HUD shows 🌿 Herbalism progress

#### 4. **Woodcutting Skill**
- Break any log (oak, spruce, birch, jungle, acacia, dark oak, crimson stem, warped stem, mangrove, cherry)
- **Expected:** HUD shows 🪓 Woodcutting progress

#### 5. **Fishing Skill**
- Fish with a fishing rod
- **Expected:** HUD shows 🎣 Fishing progress when you catch something

#### 6. **Combat Skill**
- Kill mobs (zombies, skeletons, etc.)
- **Expected:** HUD shows ⚔ Combat progress
- Kill another player (if PvP enabled)
- **Expected:** More experience for player kills

#### 7. **Defense Skill** ⭐ (Important!)
- Take damage from mobs or players
- **Expected:** 
  - HUD shows 🛡 Defense progress
  - Your max health increases as defense level increases
  - Your attack damage increases (strength bonus)
  - Your armor increases (defense bonus)
- Kill a player in PvP
- **Expected:** Bonus defense experience

#### 8. **Farming Skill**
- Harvest crops (wheat, carrots, potatoes, beetroot)
- Breed animals (feed two animals to breed)
- **Expected:** HUD shows 🌾 Farming progress

#### 9. **Smithing Skill**
- Craft any item
- **Expected:** HUD shows 🔨 Smithing progress

#### 10. **Archery Skill**
- Hit entities with arrows from a bow
- **Expected:** HUD shows 🏹 Archery progress

#### 11. **Enchanting Skill**
- Enchant an item at an enchanting table
- **Expected:** HUD shows ✨ Enchanting progress

#### 12. **Alchemy Skill**
- Brew potions in a brewing stand
- **Expected:** HUD shows ⚗ Alchemy progress

#### 13. **Taming Skill**
- Tame a wolf (with bones) or cat (with fish)
- **Expected:** HUD shows 🐾 Taming progress

#### 14. **Acrobatics Skill**
- Take fall damage
- **Expected:** 
  - HUD shows 🤸 Acrobatics progress
  - Fall damage should be reduced based on your acrobatics level

### HUD Testing

1. **HUD Display**
   - Perform any skill activity
   - **Expected:** HUD appears above hotbar (action bar)
   - Shows: Icon, Skill Name, Level, Percentage, Experience

2. **HUD Duration**
   - Perform a skill activity
   - Wait 3 seconds (60 ticks)
   - **Expected:** HUD disappears after inactivity

3. **Level Up**
   - Gain enough experience to level up
   - **Expected:** 
     - Green "LEVEL UP!" message in chat
     - Shows skill icon, name, and new level
     - HUD updates with new level

### Defense Bonuses Testing

1. **Health Bonus**
   - Level up Defense skill
   - **Expected:** Max health increases (check with `/skillstats` or look at health bar)

2. **Strength Bonus**
   - Level up Defense skill
   - Attack a mob
   - **Expected:** Damage dealt increases

3. **Defense Bonus**
   - Level up Defense skill
   - Take damage
   - **Expected:** Damage taken decreases

### Data Persistence Testing

1. **Save Data**
   - Gain some experience in various skills
   - Type `/stop` or disconnect
   - **Expected:** Data saved to `plugins/LevelingPlugin/playerdata.yml`

2. **Load Data**
   - Reconnect to server
   - Type `/skills`
   - **Expected:** Your previous skill levels and experience are restored

### Level Cap Testing

1. **Max Level**
   - Gain experience until you reach level 50
   - **Expected:** 
     - No more experience gained
     - Progress shows 100%
     - Level stays at 50

## 🐛 Troubleshooting

### Plugin doesn't load
- Check server console for errors
- Ensure you're using Paper 1.21.1 or compatible version
- Check Java version (needs Java 21+)

### HUD doesn't show
- Check `config.yml` - ensure `hud.enabled: true`
- Try performing a skill activity
- Check if action bar is visible in your client settings

### No experience gained
- Check `config.yml` for experience values
- Ensure the activity matches a configured skill
- Check server console for errors

### Defense bonuses not working
- Rejoin the server (bonuses apply on join)
- Check `/skillstats` to see bonus values
- Ensure you have defense levels

## 📝 Test Report Template

```
Test Date: ___________
Server Version: ___________
Plugin Version: 1.0.0

✅ Commands: Working / Not Working
✅ Mining: Working / Not Working
✅ Herbalism: Working / Not Working
✅ Woodcutting: Working / Not Working
✅ Fishing: Working / Not Working
✅ Combat: Working / Not Working
✅ Defense: Working / Not Working
✅ Farming: Working / Not Working
✅ Smithing: Working / Not Working
✅ Archery: Working / Not Working
✅ Enchanting: Working / Not Working
✅ Alchemy: Working / Not Working
✅ Taming: Working / Not Working
✅ Acrobatics: Working / Not Working
✅ HUD Display: Working / Not Working
✅ Level Up Messages: Working / Not Working
✅ Data Persistence: Working / Not Working

Issues Found:
- 

Notes:
- 
```

## 🎮 Quick Test Commands

After deploying, try these in-game:
1. `/skills` - View all skills
2. Break a block (stone/log/crop) - Test HUD
3. Kill a mob - Test combat
4. Take damage - Test defense
5. `/skillstats` - View detailed stats

Happy testing! 🚀

