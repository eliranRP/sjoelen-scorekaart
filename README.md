# Sjoelen puntentelling

Papieren zettel (scoresheet) voor fysieke **sjoelbak**. Geen 3D-bak, geen spel, geen physics.

De launcher-naam is **Sjoelen puntentelling**. `Scorekaart` is alleen een interne/repo-bijnaam. Package: `com.eliranrp.sjoelenscorekaart`.

Volledig offline. Geen account, geen netwerk, geen ads.

## Bouwen

JDK 17+ (21 is prima). Android SDK met `platforms;android-36`.

```bash
./gradlew assembleDebug
./gradlew testDebugUnitTest
```

Debug-APK: `app/build/outputs/apk/debug/app-debug.apk`.

CI draait alleen `assembleDebug` + unit tests. Geen Play-upload, geen `bundleRelease`.

## Puntentelling

Spelerszicht, poorten links → rechts: **2 / 3 / 4 / 1**.

Per ronde **30 schijven**. De app telt per poort met +/− en weigert een 31e schijf.

```
punten = (n2 × 2) + (n3 × 3) + (n4 × 4) + (n1 × 1)
```

**Volle bak** (alle vier poorten minstens één schijf): huisregel-bonus **+20**. Dat is een schakelaar (standaard aan) wanneer alle vier geraakt zijn — niet de enige officiële telling.

Nieuwe ronde bewaart het wedstrijdtotaal. Reset ronde / reset wedstrijd vragen om bevestiging. Ongedaan maken draait de laatste tap terug. Speler- en teamnaam zijn optioneel en blijven lokaal bewaard.
