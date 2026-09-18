# NoteKeeper — Smart Notes Android App

<p align="center">
  <img src="https://img.shields.io/badge/Platform-Android-3DDC84?style=for-the-badge&logo=android&logoColor=white" />
  <img src="https://img.shields.io/badge/Language-Kotlin-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white" />
  <img src="https://img.shields.io/badge/Firebase-Firestore-FFCA28?style=for-the-badge&logo=firebase&logoColor=black" />
  <img src="https://img.shields.io/badge/Min_SDK-24_(Android_7.0)-brightgreen?style=for-the-badge" />
  <img src="https://img.shields.io/badge/License-MIT-blue?style=for-the-badge" />
</p>

<p align="center">
  A feature-rich Android note-taking application built with Kotlin and Firebase, offering real-time sync, priority management, and a clean Material UI.
</p>

---

## Features

| Feature | Description |
|---|---|
| Authentication | Email/password sign-up & login with Firebase Auth |
| Forgot Password | Password reset via email link |
| Create & Edit Notes | Rich note editor with title, content, and priority |
| Priority Badges | Label notes as Low / Medium / High priority |
| Trash / Recycle Bin | Deleted notes move to trash before permanent deletion |
| Real-time Sync | Notes synced instantly via Firebase Firestore |
| Dark Mode | Full dark mode support |
| Note Information | View creation date and metadata for each note |

---

## Tech Stack

- **Language:** Kotlin
- **Architecture:** Activity-based (MVP pattern)
- **Database:** Firebase Firestore (NoSQL, real-time)
- **Authentication:** Firebase Authentication
- **UI:** Android XML Layouts, Material Design Components
- **Build Tool:** Gradle (Kotlin DSL)
- **Min SDK:** 24 (Android 7.0 Nougat)
- **Target SDK:** 36

---

## Project Structure

```
NoteApp/
├── app/
│   └── src/
│       └── main/
│           ├── java/com/example/noteapp/
│           │   ├── MainActivity.kt          # Login screen
│           │   ├── SignUp.kt                # Registration screen
│           │   ├── ForgotPassword.kt        # Password reset
│           │   ├── noteActivity.kt          # Notes list (home)
│           │   ├── createnote.kt            # Create new note
│           │   ├── editnote.kt              # Edit existing note
│           │   ├── information_noteactivity.kt  # Note detail/info
│           │   ├── TrashActivity.kt         # Trash bin
│           │   └── firebasemodel.kt         # Firebase data model
│           └── res/
│               ├── layout/                  # XML layouts
│               ├── drawable/                # Icons and backgrounds
│               └── values/                 # Colors, strings, themes
└── gradle/
    └── libs.versions.toml                   # Dependency versions
```

---

## Getting Started

### Prerequisites

- **Android Studio** Hedgehog (2023.1.1) or later
- **JDK 11** or higher
- **Firebase account** (for Firestore and Auth)

### 1. Clone the Repository

```bash
git clone https://github.com/DataZenLab/notekeeper.git
cd notekeeper
```

### 2. Firebase Setup

1. Go to [Firebase Console](https://console.firebase.google.com/)
2. Create a new project (or use an existing one)
3. Add an **Android app** with package name: `com.example.noteapp`
4. Download `google-services.json` and place it in the `app/` directory
5. Enable **Firestore Database** (Start in test mode for development)
6. Enable **Firebase Authentication** then enable the Email/Password provider

### 3. Firestore Data Structure

```
users/
  {userId}/
    notes/
      {noteId}/
        title: String
        content: String
        priority: String  (Low | Medium | High)
        createdAt: Timestamp
        isDeleted: Boolean
```

### 4. Build and Run

1. Open the project in **Android Studio**
2. Wait for Gradle sync to complete
3. Connect an Android device or start an emulator (API 24+)
4. Click **Run** or press Shift + F10

---

## Security Notes

The `google-services.json` file is excluded from version control via `.gitignore`.
Never commit this file as it contains your Firebase project credentials.

---

## Roadmap

- [ ] Search and filter notes
- [ ] Color-coded note categories
- [ ] Reminder / alarm notifications
- [ ] Markdown support in note editor
- [ ] Biometric authentication

---

## Author

**DataZenLab**
- GitHub: [@DataZenLab](https://github.com/DataZenLab)

---

## License

This project is licensed under the **MIT License**.
