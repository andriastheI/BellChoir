/**
 * Filename: Tone.java
 * Author: Andrias Zelele
 * Date: 2026
 * Description:
 * Handles audio playback and coordination of the bell choir system.
 * Loads songs from files, creates choir members (threads),
 * and manages synchronized playback of notes.
 */
import java.io.*;
import java.util.ArrayList;
import java.util.List;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

public class Tone {

    /** Shared audio output line used by all threads */
    private SourceDataLine sharedLine;

    /**
     * Main entry point of the program.
     * Loads a song, creates member threads, and conducts playback.
     */
    public static void main(String[] args) throws Exception {
        final AudioFormat af =
                new AudioFormat(Note.SAMPLE_RATE, 8, 1, true, false);

        Tone t = new Tone(af);

        // Determine filename from arguments or use default
        String filename = (args.length > 0 && args[0] != null && !args[0].isBlank()) ? args[0] : "MaryLamb.txt";

        System.out.println("Loading file: " + filename);

        try {
            // Load song from file
            List<BellNote> song = t.loadSongFromFile(filename);
            System.out.println("Song loaded successfully. Playing...");

            ChoirCoordinator coordinator = new ChoirCoordinator();

            // Create member threads
            List<Member> members = t.createMembers(song, coordinator);

            List<Thread> threads = new ArrayList<>();
            for (Member m : members) {
                Thread tMember = new Thread(m);
                threads.add(tMember);
                tMember.start();
            }

            // Open audio line and begin playback
            t.openLine();
            try {
                t.conductSong(song, members, coordinator);
            } finally {
                t.closeLine();
            }

            // Wait for all threads to finish
            for (Thread th : threads) {
                th.join();
            }
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
    }

    /** Audio format used for playback */
    private final AudioFormat af;

    /**
     * Constructs a Tone object with a given audio format.
     *
     * @param af the audio format used for playback
     */
    Tone(AudioFormat af) {
        this.af = af;
    }

    /**
     * Loads a song from a file and converts it into BellNote objects.
     * Validates input and collects errors if present.
     *
     * @param filename the name of the song file
     * @return list of BellNotes representing the song
     */
    public List<BellNote> loadSongFromFile(String filename) throws IOException {
        List<BellNote> song = new ArrayList<>();
        List<String> errors = new ArrayList<>();

        InputStream input = Tone.class.getResourceAsStream("/" + filename);

        if (input == null) {
            throw new FileNotFoundException("File not found: " + filename);
        }

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(input))) {
            String line;
            int lineNumber = 0;

            while ((line = reader.readLine()) != null) {
                lineNumber++;
                line = line.trim();

                // Skip empty lines
                if (line.isEmpty()) {
                    continue;
                }

                // Split line into note and length
                String[] parts = line.split("\\s+");

                if (parts.length != 2) {
                    errors.add("Line " + lineNumber + ": invalid format -> \"" + line + "\"");
                    continue;
                }

                Note note = null;
                NoteLength length = null;

                // Parse note value
                try {
                    note = Note.valueOf(parts[0]);
                } catch (IllegalArgumentException e) {
                    errors.add("Line " + lineNumber + ": invalid note -> \"" + parts[0] + "\"");
                }

                // Parse note length
                try {
                    length = getNoteLength(parts[1]);
                } catch (IllegalArgumentException e) {
                    errors.add("Line " + lineNumber + ": invalid note length -> \"" + parts[1] + "\"");
                }

                // Add valid note to song
                if (note != null && length != null) {
                    song.add(new BellNote(note, length));
                }
            }
        }

        // Report errors if any exist
        if (!errors.isEmpty()) {
            System.err.println("Song file contains errors:");
            for (String error : errors) {
                System.err.println(error);
            }
            throw new IllegalArgumentException("Invalid song file. Fix the errors before playback.");
        }

        return song;
    }

    /**
     * Conducts the song by assigning notes to members and coordinating execution.
     * Uses synchronization to ensure notes are played in order.
     */
    void conductSong(List<BellNote> song, List<Member> members, ChoirCoordinator coordinator) {
        for (BellNote bn : song) {

            // Find a member capable of playing this note
            Member target = findMemberForNote(bn.note, members);

            if (target == null) {
                System.out.println("No member found for note: " + bn.note);
                continue;
            }

            // Reset completion flag before assigning new note
            synchronized (coordinator) {
                coordinator.noteFinished = false;
            }

            // Assign note to member thread
            target.assignNote(bn);

            // Wait until the member finishes playing the note
            synchronized (coordinator) {
                while (!coordinator.noteFinished) {
                    try {
                        coordinator.wait();
                    } catch (InterruptedException e) {
                        System.out.println("Song conduction was interrupted while waiting for note: " + bn.note);
                        return;
                    }
                }
            }
        }

        // Mark song as finished
        synchronized (coordinator) {
            coordinator.songFinished = true;
        }

        // Wake up all members so they can exit
        for (Member m : members) {
            synchronized (m) {
                m.notify();
            }
        }
    }

    /**
     * Creates choir members and assigns notes to them.
     * Each member is responsible for one or two notes.
     */
    public List<Member> createMembers(List<BellNote> song, ChoirCoordinator coordinator) {
        List<Note> uniqueNotes = new ArrayList<>();

        // Collect unique notes from the song
        for (BellNote bn : song) {
            if (!uniqueNotes.contains(bn.note)) {
                uniqueNotes.add(bn.note);
            }
        }

        List<Member> members = new ArrayList<>();

        int memberCount = 1;
        for (int i = 0; i < uniqueNotes.size(); i += 2) {

            List<Note> assigned = new ArrayList<>();
            assigned.add(uniqueNotes.get(i));

            // Assign second note if available
            if (i + 1 < uniqueNotes.size()) {
                assigned.add(uniqueNotes.get(i + 1));
            }

            Member m = new Member(
                    "Member-" + memberCount,
                    assigned,
                    coordinator,
                    this
            );

            members.add(m);
            memberCount++;
        }
        return members;
    }

    /**
     * Converts string values into NoteLength enums.
     *
     * @param value string representation of note length
     * @return corresponding NoteLength
     */
    private NoteLength getNoteLength(String value) {
        switch (value) {
            case "1":
                return NoteLength.WHOLE;
            case "2":
                return NoteLength.HALF;
            case "4":
                return NoteLength.QUARTER;
            case "8":
                return NoteLength.EIGTH;
            default:
                throw new IllegalArgumentException("Invalid note length: " + value);
        }
    }

    /**
     * Opens the shared audio line for playback.
     */
    public void openLine() throws LineUnavailableException {
        sharedLine = AudioSystem.getSourceDataLine(af);
        sharedLine.open();
        sharedLine.start();
    }

    /**
     * Closes the audio line and releases resources.
     */
    public void closeLine() {
        if (sharedLine != null) {
            sharedLine.drain();
            sharedLine.close();
            sharedLine = null;
        }
    }

    /**
     * Plays a BellNote using the shared audio line.
     * Synchronized to prevent multiple threads writing at the same time.
     */
    public synchronized void playBellNote(BellNote bn) {
        if (sharedLine == null) {
            throw new IllegalStateException("Audio line is not open.");
        }
        playNote(sharedLine, bn);
    }

    /**
     * Writes audio data to the output line for playback.
     */
    private void playNote(SourceDataLine line, BellNote bn) {
        final int ms = Math.min(bn.length.timeMs(), Note.MEASURE_LENGTH_SEC * 1000);
        final int length = Note.SAMPLE_RATE * ms / 1000;

        // Write note sound followed by short silence
        line.write(bn.note.sample(), 0, length);
        line.write(Note.REST.sample(), 0, 50);
    }

    /**
     * Finds a member capable of playing a given note.
     *
     * @param note the note to be played
     * @param members list of available members
     * @return a member that can play the note, or null if none found
     */
    private Member findMemberForNote(Note note, List<Member> members) {
        for (Member member : members) {
            if (member.canPlay(note)) {
                return member;
            }
        }
        return null;
    }
}