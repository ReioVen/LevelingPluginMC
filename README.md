# LevelingPlugin

A comprehensive Minecraft leveling system plugin for Paper/Spigot servers developed by **R3io**. This plugin adds skill-based progression with 8 unique skills, HUD displays, and powerful bonuses that enhance the gameplay experience.

## 🎮 Features

### Skills System
- **8 Unique Skills**: Mining, Excavation, Woodcutting, Combat, Defense, Farming, Smithing, and Acrobatics
- **Level Cap**: All skills can level up to level 50
- **Experience System**: Configurable experience requirements with exponential scaling
- **HUD Display**: Real-time progress display above the hotbar showing level, percentage, and experience
- **Interactive GUI System**: Beautiful chest-based GUIs for viewing skills, statistics, and leaderboards

### Skill Bonuses

#### Mining
- **2% double drop chance per level** (works with Fortune enchantment)
- Higher levels = more valuable drops

#### Excavation (NEW!)
- **1% double drop chance per level**
- **Special Drops**:
  - Level 10+: 1% Diamond drop chance
  - Level 20+: 2% Diamond drop chance
  - Level 30+: 3% Diamond drop chance
  - Level 40+: 5% Diamond + 2% Gold Ingot drop chance
  - Level 50: 7% Diamond + 3% Netherite Scrap drop chance

#### Farming
- **2% double drop chance per level**
- **Auto Replant**: At level 30+, crops automatically replant (requires seeds in inventory)
- Includes all crops: wheat, carrot, potato, beetroot, nether wart
- Includes all plants: pumpkin, melon, sugarcane, cactus, chorus fruit, cocoa, berries
- Animal breeding also gives farming experience

#### Combat
- **0.5% damage output per level** (max 25% at level 50)
- Increases your attack damage based on combat level

#### Defense
- **0.5% armor toughness per level** (max 25% at level 50)
- Reduces damage taken as you level up

#### Acrobatics
- **1% fall damage reduction per level**
- Higher levels = less fall damage

#### Smithing
- **0.1% chance per level to get 2x crafted item** (max 5% at level 50)
- When triggered, you get 2 of the item you crafted instead of 1
- Example: Craft a diamond sword → 5% chance to get 2 diamond swords!

### Commands

#### Player Commands
- `/level` - Opens interactive GUI showing all your skill levels
- `/level <skill>` - Opens detailed GUI for a specific skill (e.g., `/level MINING`)
- `/level help` - Show help menu with all commands (chat only)
- `/levelstats` - Opens detailed statistics GUI for all skills
- `/leaderboard <skill> [page]` - Opens leaderboard GUI for a skill
  - Aliases: `/lb`, `/top`
  - Example: `/leaderboard mining 1`

**Note**: All commands (except `/level help`) now open beautiful interactive GUIs instead of chat messages. GUIs feature:
- Player heads for leaderboard entries
- Clickable navigation between menus
- Progress bars and visual indicators
- Items cannot be removed from GUIs

#### Admin Commands
- `/level set <player> <skill> <level>` - Set a player's skill level (requires `leveling.admin` permission)
  - Example: `/level set PlayerName mining 25`
  - Works for both online and offline players

### Permissions

- `leveling.admin` - Admin permissions (default: op)
- `leveling.use` - Use leveling system (default: true)

## 📦 Installation

1. **Requirements**:
   - Paper/Spigot 1.21.1 or compatible
   - Java 21+

2. **Download**:
   - Download the latest JAR from [Releases](https://github.com/ReioVen/LevelingPluginMC/releases)
   - Or build from source (see Building section)

3. **Install**:
   - Place `LevelingPlugin-1.0.0.jar` in your server's `plugins/` folder
   - Restart your server
   - The plugin will create a `config.yml` file automatically

## 🔧 Configuration

The plugin creates a `config.yml` file in `plugins/LevelingPlugin/` with the following options:

### Experience Settings
```yaml
max-level: 50
experience:
  base: 100
  multiplier: 1.5
```

### Skill Experience Gains
Configure experience gained for each action in `experience-gain` section:
- Mining: Stone, deepslate, cobblestone, cobbled deepslate, coal ore, **copper ore**, iron ore, gold ore, diamond ore, emerald ore, lapis ore, redstone ore, nether gold ore, nether quartz ore, ancient debris
- Excavation: Dirt, grass, sand, gravel, clay, etc.
- Farming: Crops (wheat, carrot, potato, beetroot, nether wart), plants (pumpkin, melon, sugarcane, cactus, etc.), and animal breeding
- Woodcutting: All log types
- Combat: Player kills and mob kills
- Defense: **Actual damage taken** (not blocked hits) and PvP kills
- Smithing: Item crafting
- Acrobatics: Fall damage

### HUD Settings
```yaml
hud:
  enabled: true
  update-interval: 20  # ticks (1 second)
  display-duration: 60  # ticks (3 seconds)
```

### Bonuses
```yaml
defense-bonuses:
  toughness-per-level: 0.005  # 0.5% per level
```

## 🛠️ Building from Source

### Prerequisites
- Java 21 JDK
- Maven 3.6+

### Build Steps

1. **Clone the repository**:
   ```bash
   git clone https://github.com/ReioVen/LevelingPluginMC.git
   cd LevelingPluginMC
   ```

2. **Set Java 21** (if not default):
   ```bash
   # Windows PowerShell
   $env:JAVA_HOME = "C:\path\to\java-21"
   $env:PATH = "$env:JAVA_HOME\bin;$env:PATH"
   
   # Linux/Mac
   export JAVA_HOME=/path/to/java-21
   export PATH=$JAVA_HOME/bin:$PATH
   ```

3. **Build the plugin**:
   ```bash
   mvn clean package
   ```

4. **Find the JAR**:
   - Location: `target/LevelingPlugin-1.0.0.jar`

### Quick Build (Windows)
Use the included `build.bat` file:
```bash
.\build.bat
```

Make sure to update the Java path in `build.bat` if needed.

## 📊 Skills Overview

| Skill | Icon | How to Level | Special Features |
|-------|------|--------------|------------------|
| **Mining** | ⛏ | Break ores | 2% double drops per level |
| **Excavation** | ⛏ | Break dirt/sand/gravel | 1% double drops + special drops at higher levels |
| **Woodcutting** | 🪓 | Break logs | Standard progression |
| **Combat** | ⚔ | Kill mobs/players | 0.5% damage per level (max 25%) |
| **Defense** | 🛡 | Take damage/PvP kills | 0.5% toughness per level (max 25%) |
| **Farming** | 🌾 | Break crops/plants/breed animals | 2% double drops + auto replant at 30+ |
| **Smithing** | 🔨 | Craft items | 0.1% per level double craft chance (max 5%) |
| **Acrobatics** | 🤸 | Take fall damage | 1% fall damage reduction per level |

## 🎯 Usage Examples

### View Your Levels
```
/level                    # See all skills
/level MINING            # See mining details
/levelstats              # Detailed statistics
```

### Check Leaderboards
```
/leaderboard mining       # Top miners
/leaderboard combat 2    # Page 2 of combat leaderboard
/lb excavation           # Excavation leaderboard
```

### Admin Testing
```
/level set PlayerName mining 30
/level set PlayerName excavation 50
/level set PlayerName combat 25
```

## 📝 Data Storage

Player data is stored in:
- `plugins/LevelingPlugin/playerdata.yml`

Data is automatically:
- Loaded when players join
- Saved when players leave
- Persisted across server restarts

## 🐛 Troubleshooting

### Plugin doesn't load
- Ensure you're using Paper 1.21.1 or compatible
- Check that Java 21+ is installed
- Check server console for error messages

### HUD doesn't show
- Check `config.yml` - ensure `hud.enabled: true`
- Perform a skill activity (break a block, kill a mob)
- Verify action bar is visible in client settings

### No experience gained
- Check `config.yml` for experience values
- Ensure the activity matches a configured skill
- Check server console for errors

### Bonuses not working
- Rejoin the server (bonuses apply on join)
- Check `/levelstats` to verify bonus values
- Ensure you have the required skill levels

## 🔄 Version History

### v1.1.0 (Latest)
- **NEW**: Complete GUI system overhaul - all commands now use interactive chest GUIs
- **NEW**: All Skills GUI - beautiful overview of all 8 skills with clickable navigation
- **NEW**: Skill Detail GUI - detailed view with progress bars, bonuses, and leaderboard access
- **NEW**: Leaderboard GUI - displays player heads with rank, name, and level
- **NEW**: Skill Stats GUI - comprehensive statistics view for all skills
- **NEW**: Interactive navigation - click between menus seamlessly
- **NEW**: Items cannot be removed from GUIs - fully protected inventory system
- **NEW**: Black glass panes fill empty slots for a polished look
- **Improved**: Only `/level help` remains as chat output, all other displays use GUIs

### v1.0.1
- **Fixed**: Experience percentage now correctly shows total XP percentage (e.g., 450/1000 = 45%) instead of consecutive farming amount
- **Fixed**: Defense XP now only grants experience when player actually takes damage (not when hit but no damage due to friendly fire protection or other plugins)
- **Added**: Copper ore now gives mining experience (8 XP, same as coal)
- **Improved**: Deepslate blocks already supported for mining XP
- **Updated**: HUD and commands now display total experience correctly

### v1.0.0 (by R3io)
- Initial release by R3io
- 8 skills with level cap of 50 (Farming merged from Herbalism)
- HUD system with progress display
- Admin commands for testing
- Leaderboard system
- Comprehensive bonuses system
- Configurable double drop rates
- Silk Touch protection for ores
- All shovel-effective blocks in excavation

## 📄 License

This project is licensed under the MIT License - see the LICENSE file for details.

## 🤝 Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## 👤 Author

**R3io** - Plugin Developer

Created and developed by R3io. This plugin was built from the ground up to provide a comprehensive skill-based leveling system for Minecraft servers.

## 🙏 Acknowledgments

- Built by R3io for Paper/Spigot servers
- Uses Adventure API for modern text components
- Inspired by classic RPG leveling systems
- Special thanks to the Minecraft plugin development community

## 📞 Support

- **Issues**: [GitHub Issues](https://github.com/ReioVen/LevelingPluginMC/issues)
- **Repository**: [GitHub](https://github.com/ReioVen/LevelingPluginMC)
- **Developer**: R3io

---

**Made with ❤️ by R3io for the Minecraft community**
