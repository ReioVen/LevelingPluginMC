# Building the LevelingPlugin

## Prerequisites
- Java 21 JDK installed
- Maven 3.6+ installed

## Build Options

### Option 1: Quick Build (Recommended)
If Maven is hanging, try building with offline mode after first download:

```bash
# First time: Download dependencies
mvn dependency:resolve

# Then build offline
mvn clean package -o
```

### Option 2: IDE Build
1. Open the project in IntelliJ IDEA or Eclipse
2. Right-click on `pom.xml` → Maven → Reload Project
3. Build → Build Project (or Ctrl+F9 in IntelliJ)
4. The JAR will be in `target/LevelingPlugin-1.0.0.jar`

### Option 3: Manual Compile
If Maven continues to hang, you can compile manually:

```bash
# Compile only (no package)
mvn clean compile

# If that works, then package
mvn package
```

### Option 4: Skip Tests
Sometimes tests can cause hangs:

```bash
mvn clean package -DskipTests
```

## Troubleshooting

### If Maven hangs on dependency download:
1. Check your internet connection
2. Check Maven settings: `~/.m2/settings.xml`
3. Try using a different Maven repository mirror
4. Clear Maven cache: `rm -rf ~/.m2/repository/io/papermc`

### If you get Java version errors:
- Ensure Java 21 is installed: `java -version`
- Set JAVA_HOME to Java 21 installation

## Output
The compiled JAR will be located at:
`target/LevelingPlugin-1.0.0.jar`

Copy this to your server's `plugins/` folder to deploy.

