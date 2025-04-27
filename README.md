# Matrix Calculator App (Android + C++)

This Android app allows users to input two matrices and perform the following operations:

✅ Addition  
✅ Subtraction  
✅ Multiplication  
✅ Element-wise Division

The matrix operations are performed using **C++ native code**, interfaced with Android through **JNI**, and using a vector/matrix library like **Eigen**.

---

## 📱 Features

- Supports **any dimension matrices**.
- Simple input interface for matrix size and elements.
- Choice of operations via buttons.
- Displays the resulting matrix on the screen.

---

## ⚙️ Tech Stack

- Android (Kotlin/Java)
- C++ (Native code using Eigen or similar)
- JNI (Java Native Interface)
- CMake

---

## 🏗️ How to Build

1. Clone the repo and checkout the branch:
   ```bash
   git clone https://github.com/yourusername/matrix-wifi-app.git
   cd matrix-wifi-app
   git checkout matrix-calculator
   ```

2. Open in Android Studio.

3. Make sure **NDK** and **CMake** are installed via SDK Manager.

4. Sync Gradle and **Run** the app.

---

