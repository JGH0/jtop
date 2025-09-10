# jtop – Prozess- und System-Tool in Java (ähnlich `top`)

![License: MIT](https://img.shields.io/badge/License-MIT-blue.svg)
![Java Version](https://img.shields.io/badge/Java-21%2B-orange.svg)
![Platform](https://img.shields.io/badge/Platform-Linux%20%7C%20macOS-lightgrey)

## 📌 Projektname
**jtop – Prozess- und System-Tool in Java**  
Ein ressourcenschonendes, terminalbasiertes System-Monitoring-Tool, das die grundlegenden Aufgaben von `top` nachbildet.  
Es basiert auf **Java 21+**, kommt ohne externe Build-Tools aus und bietet eine modulare, tabbasierte Ansicht im Terminal.

---

## 👤 Ersteller / Datum
- **Ersteller:** Jürg Georg Hallenbarter  
- **Datum:** 03.09.2025  

---

## 📝 Einleitung
Das Projekt **jtop** soll eine intuitive Alternative zu `top` darstellen, entwickelt in **Java** und ausgeführt direkt im Terminal.  
Es legt Wert auf:
- minimalistische Abhängigkeiten  
- einfache Bedienung  
- konfigurierbare Tabs und Ansichten  

---

## ⚙️ Rahmenbedingungen
- **Zielplattformen:** Linux (primär), macOS (eingeschränkt kompatibel)  
- **Programmiersprache / Laufzeit:** Java 21+  
- **Build:** `javac`, keine Build-Tools erforderlich  
- **Ausführung:** CLI-Anwendung im Terminal  
- **Eingabehandling:** Terminal-Rohmodus via  
  ```bash
  ProcessBuilder("sh", "-c", "stty raw -echo </dev/tty")
