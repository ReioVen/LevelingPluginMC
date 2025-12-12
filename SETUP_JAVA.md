# Java Setup Instructions

## Problem
Maven is using Java 17, but the plugin requires Java 21.

## Solution

You need to set JAVA_HOME to point to Java 21. I see you have Java 23 installed at:
`C:\Program Files\Java\jdk-23\bin`

### Option 1: Set JAVA_HOME for this session (Temporary)
```powershell
$env:JAVA_HOME = "C:\Program Files\Java\jdk-23"
$env:PATH = "$env:JAVA_HOME\bin;$env:PATH"
```

Then verify:
```powershell
java -version
```

Should show Java 23 (which is compatible with Java 21).

### Option 2: Set JAVA_HOME permanently
1. Open System Properties → Environment Variables
2. Add new System Variable:
   - Name: `JAVA_HOME`
   - Value: `C:\Program Files\Java\jdk-23`
3. Edit PATH variable and add: `%JAVA_HOME%\bin` at the beginning

### Option 3: Use Java 21 specifically
If you have Java 21 installed, use that path instead.

### Then build:
```powershell
mvn clean package
```

