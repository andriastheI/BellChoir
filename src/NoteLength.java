/**
 * Filename: NoteLength.java
 * Author: Andrias Zelele
 * Date: 2026
 * Description:
 * Represents the duration of musical notes in the bell choir system.
 * Converts note lengths into milliseconds for timing during playback.
 */
enum NoteLength {

    WHOLE(1.0f),
    HALF(0.5f),
    QUARTER(0.25f),
    EIGTH(0.125f);

    /** The duration of the note in milliseconds */
    private final int timeMs;

    /**
     * Constructs a NoteLength and calculates its duration in milliseconds.
     *
     * @param length the fractional length of the note
     */
    NoteLength(float length) {
        timeMs = (int) (length * Note.MEASURE_LENGTH_SEC * 1000);
    }

    /**
     * Returns the duration of the note in milliseconds.
     *
     * @return the note duration in milliseconds
     */
    public int timeMs() {
        return timeMs;
    }
}