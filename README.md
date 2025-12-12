# LevelingPlugin

A comprehensive Minecraft leveling system plugin for Paper/Spigot servers that adds skill-based progression with 9 unique skills, HUD displays, and powerful bonuses.

## 🎮 Features

### Skills System
- **9 Unique Skills**: Mining, Excavation, Herbalism, Woodcutting, Combat, Defense, Farming, Smithing, and Acrobatics
- **Level Cap**: All skills can level up to level 50
- **Experience System**: Configurable experience requirements with exponential scaling
- **HUD Display**: Real-time progress display above the hotbar showing level, percentage, and experience

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

#### Herbalism
- **2% double drop chance per level**
- **Auto Replant**: At level 30+, crops automatically replant (requires seeds in inventory)

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
- **0.2% chance to return a resource** from crafting recipes
- Save materials while crafting!

### Commands

#### Player Commands
- `/level` - View all your skill levels
- `/level <skill>` - View specific skill details (e.g., `/level MINING`)
- `/level help` - Show help menu with all commands
- `/levelstats` - View detailed statistics for all skills
- `/leaderboard <skill> [page]` - View leaderboard for a skill
  - Aliases: `/lb`, `/top`
  - Example: `/leaderboard mining 1`

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
   - Download the latest JAR from [Releases](https://github.com/yourusername/LevelingPlugin/releases)
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
- Mining: Stone, coal, iron, gold, diamond, emerald, netherite
- Excavation: Dirt, grass, sand, gravel, clay, etc.
- Herbalism: Crops and plants
- Woodcutting: All log types
- Combat: Player kills and mob kills
- Defense: Damage taken and PvP kills
- Farming: Animal breeding and crop harvesting
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
   git clone https://github.com/yourusername/LevelingPlugin.git
   cd LevelingPlugin
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
| **Herbalism** | 🌿 | Break crops/plants | 2% double drops + auto replant at 30+ |
| **Woodcutting** | 🪓 | Break logs | Standard progression |
| **Combat** | ⚔ | Kill mobs/players | 0.5% damage per level (max 25%) |
| **Defense** | 🛡 | Take damage/PvP kills | 0.5% toughness per level (max 25%) |
| **Farming** | 🌾 | Harvest crops/breed animals | Standard progression |
| **Smithing** | 🔨 | Craft items | 0.2% resource return chance |
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

### v1.0.0
- Initial release
- 9 skills with level cap of 50
- HUD system with progress display
- Admin commands for testing
- Leaderboard system
- Comprehensive bonuses system

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

[Your GitHub Username]

## 🙏 Acknowledgments

- Built for Paper/Spigot
- Uses Adventure API for modern text components
- Inspired by classic RPG leveling systems

## 📞 Support

- **Issues**: [GitHub Issues](https://github.com/yourusername/LevelingPlugin/issues)
- **Discussions**: [GitHub Discussions](https://github.com/yourusername/LevelingPlugin/discussions)

---

**Made with ❤️ for the Minecraft community**
