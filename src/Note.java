/**
 * Filename: Note.java
 * Author: Andrias Zelele
 * Date: 2026
 * Description:
 * Represents musical notes used in the bell choir system.
 * Each note generates a sine wave sample that can be played as sound.
 */
enum Note {

    REST,
    A4,
    A4S,
    B4,
    C4,
    C4S,
    D4,
    D4S,
    E4,
    F4,
    F4S,
    G4,
    G4S,
    A5;

    /** Sample rate used for audio generation */
    public static final int SAMPLE_RATE = 48 * 1024;

    /** Length of one measure in seconds */
    public static final int MEASURE_LENGTH_SEC = 1;

    /** Step value used to generate sine wave samples */
    private static final double step_alpha = (2.0d * Math.PI) / SAMPLE_RATE;

    /** Base frequency for A4 note */
    private final double FREQUENCY_A_HZ = 440.0d;

    /** Maximum volume for the generated sound */
    private final double MAX_VOLUME = 127.0d;

    /** Precomputed sine wave samples for this note */
    private final byte[] sinSample = new byte[MEASURE_LENGTH_SEC * SAMPLE_RATE];

    /**
     * Constructs a Note and generates its audio sample.
     * Uses frequency calculations based on distance from A4.
     */
    Note() {
        int n = this.ordinal();

        // Skip REST (no sound)
        if (n > 0) {

            // Calculate how many half steps away from A4 this note is
            final double halfStepUpFromA = n - 1;

            // Convert half steps into exponential frequency scaling
            final double exp = halfStepUpFromA / 12.0d;

            // Calculate actual frequency of this note
            final double freq = FREQUENCY_A_HZ * Math.pow(2.0d, exp);

            // Determine how fast the sine wave progresses
            final double sinStep = freq * step_alpha;

            // Generate sine wave samples for the entire measure
            for (int i = 0; i < sinSample.length; i++) {
                sinSample[i] = (byte) (Math.sin(i * sinStep) * MAX_VOLUME);
            }
        }
    }

    /**
     * Returns the generated audio sample for this note.
     *
     * @return byte array representing the sound wave
     */
    public byte[] sample() {
        return sinSample;
    }
}