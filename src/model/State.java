package model;

/**
 * Possible states of the Pomodoro timer.
 */
public enum State {
    /** Working phase - the main countdown. */
    WORK,

    /** Short break phase after a finished pomodoro. */
    SHORT_BREAK,

    /** Long break phase after the fourth finished pomodoro. */
    LONG_BREAK,

    /** Timer paused, the countdown is frozen. */
    PAUSED
}
