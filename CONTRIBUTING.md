# 🛠️ Contributing to FluentMusic for Android

Thank you for your interest in contributing to **FluentMusic for Android**! FluentMusic is a modern open-source music player featuring dynamic Liquid Glass UI, background playback, and synced lyrics.

---

## 🏗️ Development Setup & Prerequisites

To develop and build FluentMusic, ensure your development environment satisfies:

- **JDK**: Java Development Kit 21 (Temurin / OpenJDK 21)
- **Android SDK**: Android API 35 / 37 with SDK Build-Tools 35+
- **Gradle**: Gradle 8.11+ (invoked via `./gradlew`)
- **IDE**: Android Studio Ladybug (or newer) or IntelliJ IDEA with Android plugin

---

## 🚀 Cloning & Building

1. Clone the repository with all submodules:
   ```bash
   git clone --recursive https://github.com/ks3695602-sys/FluentMusic-Android.git
   cd FluentMusic-Android
   ```

2. Build the Debug APK:
   ```bash
   ./gradlew assembleGmsMobileUniversalDebug
   ```

3. Build the Release APK:
   ```bash
   ./gradlew assembleGmsMobileUniversalRelease
   ```

---

## 🎨 Code Style & Guidelines

- **Kotlin / Jetpack Compose**: Follow standard Kotlin coding conventions and Material 3 design patterns.
- **Liquid Glass Integration**: UI components that require glass styling should use the `Modifier.liquidGlass(...)` modifier or `LiquidGlassCapsule` container provided by `moe.rukamori.archivetune.ui.component.liquidglass`.
- **Spotless Formatting**:
  ```bash
  ./gradlew spotlessApply
  ```

---

## ⚖️ Open Source Attribution

FluentMusic is distributed under the **GNU General Public License v3.0 (GPLv3)**.
- **Maintainer**: Jayprakash Sahu
- **Upstream Project**: ArchiveTune by rukamori (GPL-3.0)
- **Foundation**: InnerTune by Zion Huang (GPL-3.0)
- **Liquid Glass Engine**: AndroidLiquidGlass by Kyant0 (Apache-2.0)
