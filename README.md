# TheComicinator3000

> "Behold! The ComicINATOR 3000 — it will read your comics with GREAT EVIL EFFICIENCY!"

A Dr. Doofenshmirtz themed Android comic reader app for `.cbz` archives. Organise your comics into collections, browse pages, and let the app handle all the evil metadata parsing for you.

---

## Features

- **CBZ Reader** — open and read `.cbz` comic archives stored on device storage
- **Automatic Metadata Parsing** — reads `ComicInfo.xml` (Kavita-compatible format) from inside each archive to extract title, series, number, genre, and year
- **Cover Extraction** — automatically pulls the first image from each archive as its cover thumbnail
- **Comic Collections** — organise comics into named collections; move comics between collections
- **Library Management** — sort by name or date, filter by date range, search by title
- **Background Scanning** — WorkManager scans selected folders and syncs your library in the background without blocking the UI

---

## Screens

| Screen | Description |
|---|---|
| Onboarding | One-time storage folder picker using Android's DocumentTree intent |
| Comic Library | Grid of all collections; create, delete, and search collections |
| Comic Collection | Comics within a collection; multi-select, move, and manage |
| Comic Reader | Full-screen page viewer for the selected comic |

---

## ComicInfo.xml Support

TheComicinator3000 follows the `ComicInfo.xml` schema used by [Kavita](https://www.kavitareader.com/). When a `.cbz` archive contains a `ComicInfo.xml` file at the root, the app parses the following tags:

```xml
<ComicInfo>
  <Title>My Comic</Title>
  <Series>My Series</Series>
  <SeriesSort>My Series</SeriesSort>
  <Number>1</Number>
  <Genre>Action</Genre>
  <Year>2024</Year>
</ComicInfo>
```

Parsing happens in the background via `ComicMetadataScannerWorker` using Android's built-in `XmlPullParser`. Any CBZ without a `ComicInfo.xml` is still added to the library — it will just show without metadata.

---

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Kotlin 2.x |
| UI | Jetpack Compose + Material 3 |
| Architecture | Clean Architecture, MVVM, Repository pattern |
| DI | Hilt 2.57 |
| Database | Room |
| Preferences | DataStore |
| Background Work | WorkManager |
| Image Loading | Coil Compose |
| Navigation | Compose Navigation with type-safe routes |
| Min SDK | 30 (Android 11) |
| Target SDK | 36 (Android 16) |

---

## Project Structure

```
app/
└── src/main/java/com/deepvisiontech/thecomicinator3000/
    ├── app/                        # Application class, MainActivity
    ├── core/
    │   ├── data/                   # Room database, DataStore, shared utilities
    │   ├── di/                     # App-level Hilt modules
    │   ├── domain/                 # Shared error types (AppError, EvilResponse)
    │   └── presentation/           # Material 3 theme, colors, typography
    └── features/
        ├── comic/
        │   ├── data/
        │   │   └── local/
        │   │       ├── dao/        # ComicDao, ComicCollectionDao
        │   │       ├── entity/     # Room entities
        │   │       ├── helpers/    # Storage helpers
        │   │       ├── mappers/    # Entity <-> domain mappers
        │   │       ├── services/   # ComicScannerService, ComicExtractionService
        │   │       └── workers/    # ComicMetadataScannerWorker (XML parsing)
        │   ├── di/                 # Comic feature Hilt modules
        │   ├── domain/
        │   │   ├── model/          # Comic, ComicMetadata, ComicCollection
        │   │   ├── repository/     # Repository interfaces
        │   │   └── usecase/        # All use cases
        │   └── presentation/
        │       ├── screens/        # ComicLibraryScreen, ComicCollectionScreen, ComicScreen
        │       ├── viewmodels/     # ComicLibraryViewModel, ComicCollectionViewModel
        │       └── components/     # Reusable Compose components
        └── onboarding/
            ├── domain/
            ├── presentation/
            │   ├── screens/        # OnBoardingScreen
            │   └── viewmodels/     # OnBoardingViewModel
            └── usecase/
```

---

## Getting Started

1. Clone the repo and open in Android Studio Ladybug or later.
2. Run on a device or emulator with API 30+.
3. On first launch, grant storage access and pick a folder containing `.cbz` files.
4. The app scans the folder, extracts covers, and parses `ComicInfo.xml` in the background.

---

## Roadmap

- **Audio playback per page** — attach audio clips to specific comic pages so sound plays as you read. The `ComicInfo.xml` schema will be extended to support audio annotations as part of this feature.
- Reading progress tracking
- Bookmarks
