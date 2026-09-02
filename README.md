# Sjoelen Scorekaart

Dutch-first Android **scorekaart** for sjoelen (sjoelbak). Local puntentelling only — not a physics game, not 3D, not an account app.

Package: `com.eliranrp.sjoelenscorekaart`  
Play listing name (later): **Sjoelen Scorekaart**

## What it does

- Counts schijven in the four poorten **2 · 3 · 4 · 1** (left to right as you face the bak)
- 30 schijven per player per round; discs not in a poort score **0**
- Complete set / *volle bak*: at least one schijf in every poort → that set is **20** points (not 2+3+4+1=10). Extra complete sets are also 20 each; leftover schijven in a poort score face value
- Several players, running totals, next round, undo last change, reset with confirm
- Huge + / − targets per poort, optional names
- Default UI language is Dutch (`values/strings.xml`). English is fallback only (`values-en`)

No INTERNET permission, no ads, no tracking, no accounts.

## Rules implemented (NSB / common sjoelen)

```
sets = min(poort2, poort3, poort4, poort1)
score = 20×sets + 2×(poort2−sets) + 3×(poort3−sets) + 4×(poort4−sets) + 1×(poort1−sets)
```

Example: 1-1-1-1 = **20**. 7-7-9-7 = **148** (maximum with 30 schijven).

## Build

JDK 17+ and Android SDK 35 (`compileSdk` / `targetSdk` 35, `minSdk` 26).

```bash
./gradlew assembleDebug
./gradlew test
```

Debug APK: `app/build/outputs/apk/debug/app-debug.apk`

There is no Play upload config and no production GitHub Actions in this repo.
