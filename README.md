# RSS File Manager

RSS File Manager is a native Android file-management application by **Razeen Secure Solution (RSS)**.

The project is designed to provide a modern, secure and lightweight file-management experience with local storage management, advanced file operations, search, transfers, cloud/network storage and security features.

> **Project status:** Initial repository setup. Features are being implemented incrementally and must be verified before being marked complete.

## Project identity

- **App name:** RSS File Manager
- **Android application ID:** `com.riyaz.rssfilemanager`
- **Repository:** `riyazamra1/RSS-File-Manager`
- **Company:** Razeen Secure Solution
- **Company since:** 2015
- **Website:** https://www.rsscctvsolution.eu.cc
- **Support:** rsscctvsolution@gmail.com

## Product direction

RSS File Manager is intended to provide:

- Internal and removable-storage browsing
- USB OTG support where permitted by Android
- List and grid file views
- Dual-pane file management on suitable devices
- Copy, move, rename, delete and share
- Multi-selection and batch operations
- File properties and metadata
- Favorites and recent files
- Search, sorting and filtering
- Transfer Manager with progress and resumable operations
- Archive creation and extraction
- Storage analysis
- Large-file and duplicate-file discovery
- Trash/recovery workflows where Android permissions allow
- Secure/private files and authenticated encryption
- Biometric protection where supported
- Media and document preview
- Cloud-storage integrations
- Network-storage integrations
- FTP/Web file-server and Wi-Fi transfer capabilities where implemented
- Modern light, dark and system appearance modes

Features will only be listed as completed after they are implemented and verified in the repository.

## Design and branding

RSS File Manager follows the RSS project design system:

- Original **Razeen Secure Solution** logo only; it must not be recolored, stretched, redesigned or distorted.
- Premium black/gold brand accents where appropriate.
- Modern, clean Android UI.
- Colorful functional icons.
- Glassmorphism-inspired navigation where appropriate.
- Light appearance uses a clean white background and white cards with minimal shadow.
- Dark appearance uses a suitable dark surface hierarchy.
- System appearance follows the device theme.
- Accessibility, readable typography, spacing and touch targets are treated as first-class requirements.

The application may take functional inspiration from established file managers, but must not copy proprietary branding, assets, source code or exact UI designs.

## Architecture principles

The implementation should remain:

- Native Android
- Offline-first for local file operations
- Modular and maintainable
- Permission-aware and compatible with modern Android storage APIs
- Secure by default
- Responsive on phones, tablets and Chromebooks where practical
- Efficient with large directories and media thumbnails
- Honest about Android platform limitations

A central file-operation layer should coordinate copy, move, delete, rename and transfer operations so that progress, cancellation, collision handling and error reporting remain consistent.

## Security

Security-sensitive functionality should use Android platform security facilities wherever possible, including Android Keystore and BiometricPrompt.

Private/encrypted content must use authenticated encryption and must never store credentials, API keys or signing secrets in source control.

## Development rules

1. Inspect the current repository before making changes.
2. Preserve working functionality.
3. Do not modify unrelated RSS repositories.
4. Do not claim a feature is complete without verification.
5. Build and test changes before declaring them verified.
6. Never commit secrets, signing keys or local machine configuration.
7. Keep Android package/application IDs under the RSS convention: `com.riyaz.<appname>`.

## Build

The project is being established as a native Android project. Build instructions will be expanded as the Gradle/Android project is implemented.

Typical local verification will use the project's Gradle wrapper, for example:

```bash
./gradlew assembleDebug
```

The exact build variants and requirements will be documented here once the Android project structure is established.

## License

This repository is proprietary software of Razeen Secure Solution. See [LICENSE](LICENSE).

The absence of an open-source license does not grant permission to copy, modify, redistribute or commercially exploit the source code or RSS branding.

## Contact

**Razeen Secure Solution**

- Website: https://www.rsscctvsolution.eu.cc
- Email: rsscctvsolution@gmail.com
- Services: Mobile and PC software development, CCTV camera installation, networking and system administration.

---

© 2015–2026 Razeen Secure Solution. All rights reserved.
