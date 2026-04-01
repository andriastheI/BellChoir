/**
 * Filename: ChoirCoordinator.java
 * Author: Andrias Zelele
 * Date: 2026
 * Description:
 * Coordinates communication between choir member threads.
 * Tracks when a note has finished playing and when the entire song is complete.
 */
public class ChoirCoordinator {

    /** Indicates that a note has finished playing */
    boolean noteFinished = false;

    /** Indicates that the entire song has finished */
    boolean songFinished = false;
}