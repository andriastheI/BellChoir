/**
 * Filename: Member.java
 * Author: Andrias Zelele
 * Date: 2026
 * Description:
 * Represents a choir member that runs as a thread.
 * Each member waits until assigned a note, plays it,
 * and notifies the coordinator when finished.
 */
import javax.sound.sampled.LineUnavailableException;
import java.util.List;

public class Member implements Runnable {

    /** Name of the choir member */
    private final String name;

    /** Notes this member is capable of playing */
    private final List<Note> assignedNotes;

    /** Shared coordinator for thread communication */
    private final ChoirCoordinator coordinator;

    /** Tone generator used to play sounds */
    private final Tone tone;

    /** The note currently assigned to this member */
    private BellNote noteToPlay;

    /** Indicates whether this member should play a note */
    private boolean shouldPlay = false;

    /**
     * Constructs a Member with assigned notes and shared coordinator.
     */
    public Member(String name, List<Note> assignedNotes, ChoirCoordinator coordinator, Tone tone) {
        this.name = name;
        this.assignedNotes = assignedNotes;
        this.coordinator = coordinator;
        this.tone = tone;
    }

    /**
     * Returns the name of the member.
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the list of notes this member can play.
     */
    public List<Note> getAssignedNotes() {
        return assignedNotes;
    }

    /**
     * Checks if this member can play a specific note.
     */
    public boolean canPlay(Note note) {
        return assignedNotes.contains(note);
    }

    /**
     * Assigns a note to this member and wakes the thread.
     */
    public synchronized void assignNote(BellNote bellNote) {
        this.noteToPlay = bellNote;
        this.shouldPlay = true;
        notify();
    }

    /**
     * Main thread execution loop.
     * Waits for a note, plays it, and notifies the coordinator.
     */
    @Override
    public void run() {
        while (true) { // Keep the thread alive to continuously process notes
            BellNote current;

            synchronized (this) {
                // Wait until a note is assigned
                while (!shouldPlay) {

                    // Check if the song has finished before continuing
                    synchronized (coordinator) {
                        if (coordinator.songFinished) {
                            // Exit thread if no more work is needed
                            return;
                        }
                    }

                    try {
                        wait(); // Put thread to sleep until notified
                    } catch (InterruptedException e) {
                        // Exit safely if interrupted
                        return;
                    }
                }

                // Retrieve assigned note and reset play flag
                current = noteToPlay;
                shouldPlay = false;
            }

            try {
                // Play the assigned note using the tone generator
                tone.playBellNote(current);
            } catch (IllegalStateException e) {
                e.printStackTrace();
                // Exit if playback fails
                return;
            }

            // Notify coordinator that this note has finished playing
            synchronized (coordinator) {
                coordinator.noteFinished = true;
                // Wake up coordinator thread
                coordinator.notify();
            }
        }
    }
}