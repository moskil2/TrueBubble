# TrueBubble

**TrueBubble** is a free, ad-free spirit level app for Android — precise, clean, and built with passion.

## Features

- **Two level modes**
  - Linear level (Poziomica) — dual-axis horizontal and vertical vials with real-time pitch & roll readout
  - Bullseye level (Libella) — circular 2D level for simultaneous pitch and roll
- **Bubble customization** — 9 bubble colors including metallic gold and silver
- **High-contrast mode** — improved bubble visibility
- **Sensor calibration**
  - Auto calibration (50-sample average against a reference level)
  - Manual calibration — fine-tune Pitch and Roll offset in 0.01° steps or by direct input
- **Position lock** — remember a reference angle and compare in real time
- **Level feedback** — optional sound and vibration signal when 0° is reached
- **Dark / Light theme**
- **10 languages** — Polski, English, Español, Deutsch, Français, Português (BR), العربية, Русский, Bahasa Indonesia, 日本語
- No ads, no tracking, no internet permissions

## Download

TrueBubble is currently in **closed testing** on Google Play, so the install link only works for testers. It takes two clicks to get in:

1. **[Join the tester group](https://groups.google.com/g/truebubble)** - sign in with the same Google account you use on the Play Store, then hit "Join group".
2. **[Open the Play Store testing link](https://play.google.com/apps/testing/app.spotrobotics.truebubble)** on your phone - once you're a member, Google Play unlocks the install button for you automatically.

That's it - after the first install, TrueBubble updates itself through Google Play like any other app.

## Screenshots

_Coming soon_

## Requirements

- Android 8.0 (API 26) or higher
- Accelerometer / gravity sensor

## Build

```bash
git clone https://github.com/moskil2/TrueBubble.git
cd TrueBubble
./gradlew assembleDebug
```

Output APK: `app/build/outputs/apk/debug/app-debug.apk`

## Changelog

### v0.21
- Marker lines (zero-position marks) drawn on top of bubble in both linear and bullseye modes — matches real-world vial appearance
- Manual calibration: fixed text field input (minus sign, comma, backspace now work correctly)
- Calibration button renamed to "Start auto calibration" in all languages
- "Manual Correction" section renamed to "Manual Calibration" in all languages with updated description
- Section label font size increased in calibration screen
- Mini vials in calibration enlarged, live angle readout font increased
- Minus button in manual calibration highlighted green (same as plus button)
- Gold / Silver bubble color now correctly applied in all mini vials (Libella mode and Calibration screen)

### v0.20
- Mini vials enlarged in manual calibration; live angle readout font size increased
- Section labels in calibration screen enlarged
- Minus button in manual calibration highlighted green
- Gold/Silver bubble color fixed in Libella mini vials and Calibration screen

### v0.19
- Libella (bullseye) mode: marker lines now drawn on top of bubble
- Linear level: marker lines now drawn on top of bubble
- Mini vials in Libella mode: marker lines on top
- Manual calibration text input fixed (flag-based editing prevents sensor overwrite)
- "Start auto calibration" button label updated across all 10 languages

### v0.18
- Manual calibration section renamed from "Manual Correction" to "Manual Calibration" in all languages
- Updated calibration description text in all languages
- Bubble color gold/silver fixed in Calibration screen mini vials
- isMetallicBubble state added to CalibrationViewModel

### v0.17
- Mini vials enlarged in calibration (vertical: 18×40 → 24×54 dp, horizontal: 48×18 → 62×24 dp)
- Live angle readout font: 14sp → 18sp
- Section label font: 11sp → 13sp
- Minus button green highlight in manual calibration

### v0.16
- Libella mode mini vials: isMetallic parameter added (gold/silver bubble fix)
- CalibrationScreen: isMetallic passed through OffsetAdjustRow to mini vials

### v0.15
- Gold and Silver metallic bubble styles added
- Bubble color selector expanded to 9 options
- Metallic rendering for all vial types

### v0.11
- Initial release
- Linear level + Bullseye level modes
- Auto calibration
- Manual offset correction
- 10 language support
- Dark/Light theme

## Privacy

TrueBubble does not collect any personal data. All data (calibration, settings) is stored locally on your device only. No internet connection is required or used.

## License

© TrueBubble — All rights reserved.
