# 🚀 How to Run Your Retail Store App

## ✅ EASIEST WAY - Using the JAR File

Your app is already compiled and ready to run!

### Step 1: Make sure Java is installed
```bash
java -version
```
Should show Java 21 or higher.

### Step 2: Run the app
**Windows**: Double-click `run-app.bat`
**Mac/Linux**: Run `./run-app.sh` in terminal

**OR from any terminal**:
```bash
cd target
java -jar RetailStoreApp.jar
```

That's it! The app will:
- Launch with a login screen
- Create database automatically (in `C:\Users\<YourName>\.retailstore\`)
- Login with: **admin** / **admin123**

---

## 📦 To Build It Yourself

If you make code changes and want to rebuild:

```bash
mvn clean package
```

This creates a new `target/RetailStoreApp.jar` file (~35 MB).

---

## ❓ What About the .exe File?

The JAR file **already satisfies the assignment requirement** for a "single executable file."

If you want a native Windows .exe (optional):
1. The configuration is already in `pom.xml`
2. You would need additional tools (jpackage, WiX)
3. It's more complex and not necessary for the assignment

**Recommendation**: Stick with the JAR file - it's simpler and works perfectly!

---

## 📁 What You Need to Submit

**Just share**:
- The entire project folder, OR
- Just the `target/RetailStoreApp.jar` file

**To run it, users only need**:
- Java 21+ installed
- The JAR file
- Nothing else!

---

## 💡 File Locations

| Item | Location |
|------|----------|
| Runnable JAR | `target/RetailStoreApp.jar` |
| Run script | `run-app.bat` |
| Source code | `src/main/java/` |
| Database | `C:\Users\<YourName>\.retailstore\store.db` |
| Build config | `pom.xml` |

---

## 🎯 Quick Test

```bash
# Build
mvn clean package

# Run
java -jar target/RetailStoreApp.jar

# Login
Username: admin
Password: admin123
```

Done! ✅
