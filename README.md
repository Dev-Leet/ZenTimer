# ZenTimer 🧘‍♂️⏳

![Kotlin](https://img.shields.io/badge/Kotlin-1.9.0-purple) ![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-Material3-blue) ![License](https://img.shields.io/badge/License-MIT-green) ![Platform](https://img.shields.io/badge/Platform-Android-orange)

An elegant, minimalistic countdown timer designed for deep focus and relaxation. **ZenTimer** combines a distraction-free interface with soothing micro-animations to help you stay in the zone.

Built entirely with **Kotlin** and **Jetpack Compose**.

---

## 📸 Screenshots

| Dark Mode | Light Mode | Settings |
|:---------:|:----------:|:--------:|
| ![Dark Mode](docs/screenshots/dark_mode.png) | ![Light Mode](docs/screenshots/light_mode.png) | ![Settings](docs/screenshots/settings.png) |

*(Note: Add your screenshots to a `docs/screenshots` folder)*

---

## ✨ Features

- **🎯 Focus-First Design**: A clean, large digital display that keeps you centered.
- **🌬️ Breathing Animation**: A subtle, rhythmic pulsing animation when the timer is active to mimic a breathing cadence.
- **🎨 Dynamic Themes**: 
  - **Dark Mode**: Optimized for OLED screens (True Black).
  - **Light Mode**: High-contrast, airy design.
- **🔡 Typography Options**: Customize your vibe with **Modern** (Sans-Serif), **Digital** (Monospace), or **Classic** (Serif) fonts.
- **👆 Gesture Controls**:
  - **Tap** to Start/Pause.
  - **Long Press** to open Settings/Edit time.
- **⚙️ Customization**: 
  - Quick Presets (10m, 25m, 45m, 60m).
  - **Custom Time Selector**: Set exact hours, minutes, and seconds.
- **🔋 Always On**: Keeps your screen awake while the timer is running so you never lose track of time.

---

## 🛠️ Tech Stack

- **Language**: [Kotlin](https://kotlinlang.org/)
- **UI Toolkit**: [Jetpack Compose](https://developer.android.com/jetpack/compose) (Material 3)
- **Architecture**: MVVM (Model-View-ViewModel)
- **Concurrency**: Kotlin Coroutines & Flows
- **Components**:
  - `ViewModel`: State management.
  - `StateFlow`: Reactive UI updates.
  - `Canvas` & `Animation`: Custom drawing and breathing effects.
  - `ModalBottomSheet`: Settings interaction.

---

## 🚀 Getting Started

### Prerequisites
- Android Studio Hedgehog (2023.1.1) or newer.
- JDK 17.
- Android SDK API 34 (UpsideDownCake).

### Installation

1.  **Clone the repository**
    ```bash
    git clone https://github.com/StartYourFork/ZenTimer.git
    cd ZenTimer
    ```

2.  **Open in Android Studio**
    - Launch Android Studio.
    - Select **Open** and navigate to the cloned `ZenTimer` directory.

3.  **Sync & Build**
    - Allow Gradle to sync dependencies.
    - Click the **Run** ▶️ button (Shift + F10) to deploy to an emulator or connected device.

---

## 📂 Project Structure

```
com.example.zenfocus
├── MainActivity.kt          # Entry & Main Component
├── viewmodel/
│   └── TimerViewModel.kt    # Logic for timer, state, and themes
└── ui/
    └── theme/               # Material 3 Theme definitions
        ├── Color.kt
        ├── Theme.kt
        └── Type.kt
```

---

## 🤝 Contributing

Contributions are welcome! Please follow these steps:

1.  Fork the Project.
2.  Create your Feature Branch (`git checkout -b feature/AmazingFeature`).
3.  Commit your Changes (`git commit -m 'Add some AmazingFeature'`).
4.  Push to the Branch (`git push origin feature/AmazingFeature`).
5.  Open a Pull Request.

---

## 📄 License

Distributed under the MIT License. See `LICENSE` for more information.

---

## 📧 Contact

**Dev** - [devav@example.com](mailto:devav@example.com)

Project Link: [https://github.com/StartYourFork/ZenTimer](https://github.com/StartYourFork/ZenTimer)
