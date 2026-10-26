# Gemini Player (Android)

A user-controlled Android accessibility helper with manual tap/swipe actions, visible screen-text reading, and typing text explicitly supplied by the user into the currently focused editable field.

## Build APK

This repository includes a GitHub Actions workflow at `.github/workflows/build-apk.yml`. Push the project to GitHub, open **Actions**, run or wait for **Build Android APK**, then download the `GeminiPlayer-debug-apk` artifact. The APK is a debug build intended for personal testing.

## Install

Download `app-debug.apk` from the workflow artifact, transfer it to your Android phone, open it, and allow installation from that source if Android asks. Open Gemini Player and use **Enable accessibility service**. Android requires the user to explicitly enable an accessibility service in Settings.

## Scope and privacy

- Actions are initiated by the user from the app.
- Text entry only inserts text that the user has typed into Gemini Player; it does not invent survey answers or submit forms.
- Screen text is read locally and displayed in the app; this project does not upload it to a server.
- Accessibility access is powerful. Enable it only if you understand and trust the code. Use only on screens/accounts you are authorized to control, answer surveys truthfully, and respect game/service rules.
