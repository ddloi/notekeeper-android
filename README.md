# 📱 NoteKeeper — Smart Native Android Notes Application

<p align="center">
  <img src="https://img.shields.io/badge/Platform-Android-3DDC84?style=for-the-badge&logo=android&logoColor=white" />
  <img src="https://img.shields.io/badge/Language-Kotlin-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white" />
  <img src="https://img.shields.io/badge/Firebase-Firestore-FFCA28?style=for-the-badge&logo=firebase&logoColor=black" />
  <img src="https://img.shields.io/badge/Min_SDK-24_(Android_7.0)-brightgreen?style=for-the-badge" />
  <img src="https://img.shields.io/badge/Developer-ddloi-58A6FF?style=for-the-badge&logo=github&logoColor=white" />
</p>

<p align="center">
  A feature-packed native Android note-taking mobile application built with Kotlin and Google Firebase Cloud Firestore (real-time NoSQL synchronization), engineered by <strong>Đặng Đại Lợi (<a href="https://github.com/ddloi">@ddloi</a>)</strong>.
</p>

---

## 🌟 Key Features

| Feature | Technical Implementation |
| :--- | :--- |
| **Authentication** | Secure Email/Password registration & sign-in powered by Firebase Authentication |
| **Real-time Sync** | Automatic bidirectional note synchronization via Firebase Cloud Firestore |
| **Priority Classification** | Color-coded priority classification (High, Medium, Low) for task organization |
| **Recycle Bin / Trash** | Two-phase soft deletion with note recovery and permanent purging |
| **Offline-First Resilience** | Local cache persistence allows reading and editing notes without active internet |
| **Material 3 Design** | Modern dark and light theme palettes following Material Design Guidelines |

---

## 🏗️ Architecture & Tech Stack

- **Language:** Kotlin
- **Architecture Pattern:** Model-View-Presenter (MVP) with clean separation of UI and Firebase services
- **Cloud Database:** Firebase Firestore (Document-based real-time NoSQL)
- **Identity & Auth:** Firebase Authentication
- **Build System:** Gradle (Kotlin DSL, Target SDK 36, Min SDK 24)

```text
app/src/main/
├── java/com/example/noteapp/
│   ├── MainActivity.kt          # Authentication & Login Controller
│   ├── SignUp.kt                # User Registration Controller
│   ├── noteActivity.kt          # Primary Notes Grid / Feed
│   ├── createnote.kt            # Note Creation with Priority Selector
│   ├── editnote.kt              # Real-time Note Editing
│   ├── TrashActivity.kt         # Soft-delete Recovery & Purge Bin
│   └── firebasemodel.kt         # Firestore Note Data Entity
└── res/
    ├── layout/                  # Responsive XML Layouts
    └── values/                  # Material Color Tokens & Typography
```

---

## 🚀 Getting Started

### Prerequisites
- **Android Studio** (Hedgehog 2023.1.1 or newer)
- **JDK 17+**
- Active **Google Firebase** project

### 1. Clone the Repository
```bash
git clone https://github.com/ddloi/notekeeper-android.git
cd notekeeper-android
```

### 2. Firebase Configuration
1. Open the project in **Android Studio**.
2. Go to [Firebase Console](https://console.firebase.google.com/) and register an Android app with package:
   `com.example.noteapp`
3. Download `google-services.json` and place it in the `app/` directory.
4. Enable **Firestore Database** and **Firebase Authentication** (Email/Password).

### 3. Build & Run
Connect an Android device (USB debugging enabled) or start an Android Emulator, then click **Run (Shift + F10)**.

---

## 👨‍💻 Author & Contact

- **Developer:** Đặng Đại Lợi
- **GitHub:** [@ddloi](https://github.com/ddloi)
- **Email:** [dangdailoi732006@gmail.com](mailto:dangdailoi732006@gmail.com)
- **LinkedIn:** [ddloi](https://www.linkedin.com/in/ddloi)
- **Institution:** HUFLIT (Ho Chi Minh City University of Foreign Languages & Information Technology)
