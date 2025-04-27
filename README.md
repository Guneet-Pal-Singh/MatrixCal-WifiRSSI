# WiFi Signal Strength Logger App (Android)

This Android app logs the **Received Signal Strength (RSS)** of WiFi Access Points across different locations.

✅ Logs 100 signal strength entries per location  
✅ Supports **at least three distinct locations** (e.g., Home, Office, Cafe)  
✅ Displays collected data in a matrix form

---

## 📱 Features

- Scan nearby WiFi networks using **WifiManager**.
- Collect RSSI values efficiently.
- Store 100 samples for each identified location.
- Display logged matrices on the app screen.

---

## ⚙️ Tech Stack

- Android (Kotlin/Java)
- Android WifiManager API
- Room/SQLite (optional for storing logs)
- Jetpack ViewModel (optional for clean state handling)

---

## 🏗️ How to Build

1. Clone the repo and checkout the branch:
   ```bash
   git clone https://github.com/Guneet-Pal-Singh/MatrixCal-WifiRSSI
   cd MatrixCal-WifiRSSI
   git checkout wifi-signal-logger
   ```

2. Open in Android Studio.

3. Make sure **Location** and **WiFi** permissions are properly set.

4. Sync Gradle and **Run** the app on a physical device (WiFi scans don't work properly on most emulators).

---


✅ **Important**: Location Services must be turned ON during WiFi scanning.

---

