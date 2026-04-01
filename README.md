# 🎵 Bell Choir Threading Lab (Lab02)

**Author:** Andrias Zelele  
**Course:** Operating Systems  
**Date:** 2026

---

## 📌 Project Description

This project simulates a **bell choir system** using **multithreading in Java**.

Each choir member is represented as a **thread** that:
- Waits for a note assignment
- Plays the assigned note
- Notifies the coordinator when finished

The system demonstrates key operating system concepts including:
- Thread synchronization (`wait()`, `notify()`)
- Shared resource management
- Producer-consumer coordination

Additionally, the program generates real audio using **Java Sound API**, where each musical note is represented by a sine wave.

---

## 🧠 Key Concepts Demonstrated

- Multithreading with `Runnable`
- Synchronization using `synchronized`
- Thread communication using `wait()` and `notify()`
- Producer-Consumer pattern
- Audio generation using sine waves
- Build automation using Apache Ant


## 🏗️ Project Structure
```text
Lab02/
├── src/
│   ├── Tone.java
│   ├── Member.java
│   ├── ChoirCoordinator.java
│   ├── BellNote.java
│   ├── Note.java
│   ├── NoteLength.java
│   └── *.txt (song files)
├── dist/ (generated after build)
├── build.xml
└── README.md
```

## ⚙️ Requirements

- Java JDK 17+
- Apache Ant

---

## ▶️ How to Run the Program (Using Ant)

### 1. Navigate to project directory
``` bash
cd Lab02
```
### 2. Compile the project
``` bash
ant compile
```
### 3. Run the program (default song)
``` bash
ant run
```
### 4. Run with a specific song file
``` bash
ant run -Dsong=YourSong.txt
```
---

## 🎶 Song File Format

Each line in the song file must follow this format:

NOTE LENGTH

Example:
A4 4
C4 2
E4 1
REST 4

---

## 🧵 How It Works

1. The program reads a song file and converts it into BellNote objects
2. Unique notes are assigned to different Member threads
3. The ChoirCoordinator manages synchronization
4. Each thread waits, plays, and notifies
5. Tone handles audio playback

---

## 🚀 Features

- Multi-threaded note playback
- Real-time audio generation
- File-based song input
- Ant-based build system

---

## 🌐 GitHub Usage

This project is hosted on GitHub for version control and collaboration.

### 🔹 Clone the Repository
``` bash
git clone https://github.com/andriastheI/BellChoir.git
```
### 🔹 Navigate into the Project
``` bash
cd Lab02
```
### 🔹 Run the Program
``` bash
ant run
```
### 🔹 Run with a specific song file
``` bash
ant run -Dsong=YourSong.txt
```
---

## 📝 Notes

- Ensure Apache Ant is installed before running the project
- Song files must be placed inside the src/ directory
- Invalid song formats will produce errors during loading

---

## 🙌 Acknowledgment

Documentation guidance, and development assistance were supported by ChatGPT (OpenAI).