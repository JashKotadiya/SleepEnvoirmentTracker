# Sleep Environment Tracker 🌙

An intuitive Android application built with **Jetpack Compose** that monitors ambient light and noise levels to provide data-driven insights into your sleep environment.

---

## 🚀 Overview
The **Sleep Environment Tracker** is a personal project designed to bridge the gap between hardware sensor data and user-friendly health insights. By leveraging the device's light sensor and microphone, the app records environmental conditions throughout the night, visualizes trends via custom graphs, and provides actionable feedback to improve sleep quality.



## ✨ Key Features
* **Live Sensor Tracking:** Real-time monitoring of ambient light (Lux) and noise levels (dB).
* **Interactive Visualizations:** A custom-drawn `Canvas` graph that plots environmental fluctuations over time.
* **Intelligent Analysis:** Automated "Morning Analysis" reports that evaluate environment quality based on average and peak sensor thresholds.
* **Persistent History:** Securely saves past sessions using **GSON** and **SharedPreferences** for long-term tracking.
* **Theme Flexibility:** Full support for Material 3 Dynamic Theming with a manual Light/Dark mode toggle.
* **Lifecycle Aware:** Robust handling of hardware resources (Microphone/Sensors) to ensure app stability and battery efficiency.

## 🛠 Tech Stack
| Category | Technology |
| :--- | :--- |
| **Language** | Kotlin |
| **UI Framework** | Jetpack Compose (Material 3) |
| **Architecture** | Repository Pattern, State Hoisting |
| **Hardware APIs** | SensorManager (Light), MediaRecorder (Noise) |
| **Persistence** | SharedPreferences + GSON |
| **Concurrency** | Kotlin Coroutines |

## 🏗 Architecture & Design
The app follows modern Android development practices by decoupling hardware logic from the UI layer:

- **`SleepRepository`**: Acts as the single source of truth for active session data and history management.
- **Hardware Managers**: `LightSensorManager` and `NoiseMonitor` encapsulate complex hardware interaction, providing clean streams of data to the repository.
- **State Hoisting**: UI components like `HomeScreen` are decoupled from logic, receiving state as parameters and communicating changes via lambdas.



## 🧪 Technical Highlights (For Recruiters)
During the development of this project, I tackled several real-world Android challenges:

* **Custom Graphics:** Built a custom `SleepGraph` using the Compose `Canvas` API, implementing mathematical path scaling to accurately map sensor readings to screen coordinates.
* **Resource Management:** Implemented safe cleanup in `onDestroy` to ensure hardware sensors and the `MediaRecorder` are released, preventing memory leaks and system crashes.
* **Permission Handling:** Managed sensitive runtime permissions (Audio Recording) using the modern `ActivityResultLauncher` API.
- **Data Serialization:** Developed a mapping system to convert runtime `LocalDateTime` objects into storable JSON formats for persistence.

## 🚧 Roadmap
- [ ] **Foreground Service:** Enable background tracking so the app can monitor sleep while the screen is locked.
- [ ] **Room Database:** Transition from SharedPreferences to Room for more robust data querying and scalability.
- [ ] **Export Functionality:** Add the ability to export sleep data as a CSV for external analysis.

## 📥 Installation & Setup

### 📱 Quick Start (For Recruiters & Testers)
If you just want to try the app on your Android device without looking at the code, you can download the compiled APK directly:

1. **[Download the Latest Release APK](https://github.com/JashKotadiya/SleepEnvironmentTracker/releases/latest)**
2. Open the `.apk` file on your Android device.
3. If prompted, allow "Installation from Unknown Sources" (this is standard for apps downloaded outside the Play Store).

---

### 🛠 Build from Source (For Developers)
To explore the codebase or make modifications, follow these steps:

1. **Clone this repository:**
   ```bash
   git clone [https://github.com/JashKotadiya/SleepEnvironmentTracker.git](https://github.com/YOUR_USERNAME/SleepEnvironmentTracker.git)
