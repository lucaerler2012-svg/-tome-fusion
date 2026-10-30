# Tome Fusion (Skeleton)

Kombiniert verzauberte Buecher im Amboss durch **Addition der Level**,
begrenzt auf **Vanilla-Maximum x 2**. Fabric, Minecraft 26.3, Java 25.

**Status: unfertiges Skeleton - noch nie gebaut/getestet. Alle `TODO`-Kommentare im Code abarbeiten.**

## Projektstruktur
```
build.gradle / settings.gradle / gradle.properties
src/main/resources/fabric.mod.json
src/main/resources/tomefusion.mixins.json
src/main/java/com/example/tomefusion/TomeFusion.java        <- Einstiegspunkt
src/main/java/com/example/tomefusion/BookMerger.java        <- Level addieren + Limit
src/main/java/com/example/tomefusion/mixin/AnvilMenuMixin.java <- Amboss-Eingriff
.github/workflows/build.yml                                 <- Build auf GitHub
```

## 1. Lokal einrichten
1. JDK 25 installieren (z. B. Temurin).
2. Gradle-Wrapper fehlt noch (Binaerdatei). Einfachste Loesung: Vorlage von https://fabricmc.net/develop/template
   erzeugen und den Ordner `gradle/` sowie `gradlew`, `gradlew.bat` hierher kopieren
   (oder `gradle wrapper --gradle-version 9.5.1`, falls Gradle installiert ist).
3. Versionen in `gradle.properties` mit https://fabricmc.net/develop abgleichen.
4. `./gradlew genSources` (Quellcode lesen), `./gradlew runClient` (testen).

## 2. Auf GitHub hochladen
```
git init
git add .
git commit -m "Initial skeleton"
git branch -M main
git remote add origin https://github.com/DEIN-NAME/tome-fusion.git
git push -u origin main
```
Repo vorher auf github.com anlegen (leer, ohne README). Danach baut GitHub Actions bei jedem Push.

## 3. Bauen
`./gradlew build` -> fertige Mod: `build/libs/tome-fusion-0.1.0.jar`
(NICHT die `-sources.jar`). Die gebaute Datei ist auch unter Actions -> Artifacts downloadbar.

## 4. Auf Modrinth veroeffentlichen
1. modrinth.com -> Create a project -> Mod. Name, Slug, Beschreibung, Icon, Lizenz eintragen.
2. Environment: Client = required, Server = required (laeuft im Singleplayer auf dem integrierten Server).
3. Versions -> Create version: Jar hochladen, Version 0.1.0, Loader **Fabric**, Game version **26.3**,
   Dependency **Fabric API** (required).
4. Projekt zur Pruefung einreichen (Moderation dauert meist einige Tage).
5. Spaeter optional automatisieren mit dem Gradle-Plugin "minotaur" + GitHub Release.

## Vorschlag Name/Text
Siehe `MODRINTH.md`.
