/**
 * Filename: BellNote.java
 * Author: Andrias Zelele
 * Date: 2026
 * Description:
 * Represents a musical note to be played in the bell choir system.
 * A BellNote consists of a specific pitch (Note) and a duration (NoteLength).
 */
class BellNote {

    /** The pitch of the note to be played */
    final Note note;

    /** The duration of the note */
    final NoteLength length;

    /**
     * Constructs a BellNote with a given pitch and duration.
     *
     * @param note the musical note (pitch)
     * @param length the duration of the note
     */
    BellNote(Note note, NoteLength length) {
        this.note = note;
        this.length = length;
    }
}