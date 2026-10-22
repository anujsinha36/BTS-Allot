# BTS Allot

An offline-first Android application built with **Jetpack Compose**, **Clean Architecture**, and **Firebase** for managing volunteer (**BTS**) duty allocations across events and meetings.

---

## 📱 Screenshots

| Monthly Calendar | Duty Details & Application | Admin Creation |
| :---: | :---: | :---: |
| *[Add Screenshot]* | *[Add Screenshot]* | *[Add Screenshot]* |

---

## ✨ Features

- **📅 Interactive Calendar**: Color-coded status indicators powered by Kizitonwose Compose Calendar:
  - 🟢 **Applied**: Applied for 1+ duties on this date
  - 🔴 **Full**: All duty slots for this date are filled
  - 🔵 **Available**: Open duty slots available
- **⚡ Offline-First Architecture**: Local Room database operates as the single source of truth, synced in real-time with Firestore.
- **🚀 Optimistic Applications**: Instant local UI updates when volunteers apply for a duty, backed by atomic Firestore server increments and automatic failure rollbacks.
- **🔔 Push Notifications**: FCM notifications for newly created duties with deep linking intent payload routing directly to the calendar.
- **🛠️ Admin Management**: Tools for admins to schedule single duties or create recurring weekly duty templates.
- **🔐 Google Sign-In**: Integrated with modern Android `CredentialManager` API and Firebase Authentication.

---

## 🛠️ Tech Stack

- **UI**: Jetpack Compose, Material 3, [Kizitonwose Compose Calendar](https://github.com/kizitonwose/Calendar)
- **Architecture**: Clean Architecture + MVI/MVVM
- **Dependency Injection**: Hilt
- **Local Persistence**: Room Database
- **Backend & Services**: Firebase (Authentication, Cloud Firestore, Cloud Messaging, Cloud Functions)
- **Navigation**: Type-safe Compose Navigation (`kotlinx.serialization`)

> Detailed architecture & technical design decisions can be found in [`docs/decisions.md`](file:///docs/decisions.md).

---

## 🚀 Getting Started

### Prerequisites

- Android Studio Ladybug (2024.2.1+) or newer
- JDK 17+
- Android Device / Emulator running API Level 26+

### Setup

1. **Clone the repository**:
   ```bash
   git clone https://github.com/your-username/bts-allot.git
   cd bts-allot
   ```

2. **Configure Firebase**:
   - Create a project in the [Firebase Console](https://console.firebase.google.com/).
   - Enable **Google Sign-In** and **Cloud Firestore**.
   - Download `google-services.json` and place it in the `app/` folder.
   - Update `app/src/main/res/values/strings.xml` with your Web Client ID:
     ```xml
     <string name="default_web_client_id">YOUR_FIREBASE_WEB_CLIENT_ID</string>
     ```

3. **Build & Run**:
   - Open the project in Android Studio, sync Gradle, and run the app.

---

## 📄 License

This project is licensed under the [MIT License](LICENSE).
