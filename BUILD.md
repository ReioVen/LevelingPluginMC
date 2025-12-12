# Building the Leveling Plugin

## Prerequisites
- Java 17 or higher
- Maven 3.6 or higher
- Paper server 1.21.1

## Build Instructions

1. Navigate to the plugin directory:
   ```bash
   cd LevelingPlugin
   ```

2. Build the plugin:
   ```bash
   mvn clean package
   ```

3. The compiled JAR will be in `target/LevelingPlugin-1.0.0.jar`

4. Copy the JAR to your Paper server's `plugins` folder

## Fixing pom.xml (if needed)

If you encounter issues with the `<n>` tag in pom.xml, manually edit line 12 to change:
```xml
<n>LevelingPlugin</n>
```
to:
```xml
<name>LevelingPlugin</name>
```

## Installation on Apex Hosting

1. Build the plugin using Maven
2. Upload the JAR file to your Apex Hosting server via FTP or the file manager
3. Place it in the `plugins` folder
4. Restart your server
5. The plugin will generate configuration files automatically

## Configuration

After first run, edit `plugins/LevelingPlugin/config.yml` to customize:
- Experience values
- Defense bonuses
- HUD settings

