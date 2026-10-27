<div align="center">

  <h1>FluentMusic for Android</h1>

  <p align="center">
    <strong>Modern Liquid Glass Music Streaming & Library for Android</strong>
    <br />
    <em>An elegant, privacy-respecting music player featuring dynamic Liquid Glass refraction, progressive blur scroll headers, synced lyrics, and native local/online playback.</em>
  </p>

  <div align="center">
    <a href="https://github.com/ks3695602-sys/FluentMusic-Android/releases"><img src="https://img.shields.io/github/v/release/ks3695602-sys/FluentMusic-Android?style=for-the-badge&color=38bdf8&labelColor=0f172a&logo=github" alt="Latest Version" /></a>
    <a href="https://www.gnu.org/licenses/gpl-3.0"><img src="https://img.shields.io/badge/License-GPL--3.0-blue?style=for-the-badge&labelColor=0f172a" alt="License GPL-3.0" /></a>
    <a href="https://github.com/Kyant0/AndroidLiquidGlass"><img src="https://img.shields.io/badge/UI-Liquid%20Glass-0ea5e9?style=for-the-badge&labelColor=0f172a" alt="Liquid Glass" /></a>
  </div>

</div>

<hr />

## 🌟 About FluentMusic

**FluentMusic for Android** is an open-source, derivative music client maintained by **Jayprakash Sahu**, legally forked from the upstream project [ArchiveTune](https://github.com/rukamori/ArchiveTune) by [rukamori](https://github.com/rukamori) under the **GNU General Public License v3.0 (GPL-3.0)**.

It incorporates the advanced **Android Liquid Glass** shader engine ([Kyant0/AndroidLiquidGlass](https://github.com/Kyant0/AndroidLiquidGlass)) to deliver depth-aware UI elements including frosted refraction capsules, dynamic blur headers, and synchronized global glass controls.

---

## ✨ Features

- 💧 **Liquid Glass UI Architecture**: Powered by `io.github.kyant0:backdrop`, rendering GPU-accelerated refraction, vibrancy, and specular highlights across the bottom navigation capsule, action buttons, search bars, and toggles.
- 🌫️ **Progressive Blur Scroll Headers**: Top headers smoothly transition with backdrop blur as content scrolls beneath them across Home, Playlist, Artist, and Settings views.
- 🎛️ **Synchronized Global Liquid Glass Settings**: Dedicated appearance settings with real-time controls for blur radius, refraction intensity, and glass tint alpha.
- 🎵 **Rich Synced Lyrics & Visuals**: Full support for rich synchronized lyrics engines with word-by-word timing, translation, and background canvas artwork.
- 🔒 **Privacy Focused & Offline First**: Local cache, offline downloads, playlist backup & restore without invasive tracking.
- 🚀 **Material 3 Expressive**: Damped spring gestures, fluid page transitions, and adaptive Android 12+ dynamic tonal palettes.

---

## ⚖️ Legal & Open Source Attribution

This software is distributed under the terms of the **GNU General Public License v3.0 (GPLv3)**.

- **Maintainer & Lead Developer**: [Jayprakash Sahu](https://github.com/ks3695602-sys)
- **Original Project Attribution**: [ArchiveTune](https://github.com/rukamori/ArchiveTune) created by [rukamori](https://github.com/rukamori) (Licensed under GPL-3.0).
- **Foundation Attribution**: [InnerTune](https://github.com/z-huang/InnerTune) created by [Zion Huang](https://github.com/z-huang) (Licensed under GPL-3.0).
- **Liquid Glass Attribution**: [AndroidLiquidGlass](https://github.com/Kyant0/AndroidLiquidGlass) created by [Kyant0](https://github.com/Kyant0) (Licensed under Apache-2.0).

Per Sections 4 and 5 of GPLv3:
1. The original GPL-3.0 license is fully preserved.
2. All modifications are documented and licensed under GPL-3.0.
3. Prominent notices and upstream links are provided throughout the codebase, About page, and documentation.

---

## 📦 Download APK

Pre-built APK releases are automatically compiled and published through GitHub Actions CI:

👉 **[Download Latest APK from GitHub Releases](https://github.com/ks3695602-sys/FluentMusic-Android/releases)**

---

## 🛠️ Building From Source

### Prerequisites
- JDK 17 or JDK 21
- Android SDK (API 35+)

```bash
# Clone the repository
git clone https://github.com/ks3695602-sys/FluentMusic-Android.git
cd FluentMusic-Android

# Build Debug APK
./gradlew assembleDebug

# Build Release APK
./gradlew assembleRelease
```
The compiled APK will be located at:
`app/build/outputs/apk/debug/app-debug.apk`
