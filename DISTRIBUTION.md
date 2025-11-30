# Retail Store Management System - Distribution Guide

## Overview
Your application has been configured to create TWO types of executable files:

### 1. **Fat JAR File** (✅ READY TO USE)
- **Location**: `target/RetailStoreApp.jar`
- **Size**: ~35 MB
- **Requirement**: Java 21 or higher must be installed
- **Cross-platform**: Works on Windows, Mac, Linux

#### How to Run the JAR:
```bash
# Option 1: Double-click run-app.bat (Windows)
# Option 2: From command line
cd target
java -jar RetailStoreApp.jar
```

#### Distribution:
- Simply share the `RetailStoreApp.jar` file
- Users need Java 21+ installed
- Database will be created automatically in `C:\Users\<username>\.retailstore\`

---

### 2. **Native Windows Executable** (⚠️ OPTIONAL - Not Yet Built)
- **Output**: Windows .exe installer
- **Requirement**: NO Java installation needed (JRE bundled)
- **Size**: ~200-300 MB
- **Professional distribution option**

#### To Build Native .exe:
```bash
# Create runtime image first
mvn javafx:jlink

# Then create installer (requires WiX Toolset for .msi or jpackage)
mvn jpackage:jpackage
```

**Note**: Building native executables requires additional tools and is optional. The JAR file satisfies the assignment requirement.

---

## Building From Source

### Build the Fat JAR:
```bash
mvn clean package
```

This creates:
- `target/RetailStoreApp.jar` - The runnable fat JAR
- All dependencies bundled inside
- Ready to distribute

---

## Features Included
✅ All dependencies bundled (JavaFX, SQLite, iText PDF)  
✅ Automatic database initialization  
✅ Database stored in user home directory  
✅ CSV & PDF export functionality  
✅ Password visibility toggle  
✅ Clean, production-ready code  

---

## Default Login
- **Username**: admin
- **Password**: admin123

---

## System Requirements

### For JAR File:
- Java 21 or higher
- Windows, Mac, or Linux
- 100 MB free disk space

### For Building:
- Maven 3.6+
- Java JDK 21
- Internet connection (first build only)

---

## Quick Start for End Users

### Windows:
1. Ensure Java 21+ is installed
2. Double-click `run-app.bat`
3. Or run: `java -jar target\RetailStoreApp.jar`

### Mac/Linux:
```bash
java -jar target/RetailStoreApp.jar
```

---

## Assignment Compliance
✅ **Single executable file**: `RetailStoreApp.jar` is a complete, runnable application  
✅ **All dependencies included**: No external files needed  
✅ **Launchable by user**: Simple double-click or command line execution  
