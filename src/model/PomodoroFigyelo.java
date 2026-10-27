package model;

/**
 * Observer interface of the Pomodoro model (Observer pattern).
 * The model notifies its observers with the remaining seconds
 * and whenever the state changes.
 */
public interface PomodoroFigyelo {

    /**
     * Called when the countdown changed - every tick or right after a state transition.
     *
     * @param remainingSeconds the remaining seconds of the current phase
     */
    void onTick(int remainingSeconds);

    /**
     * Called when the timer changed its state.
     *
     * @param newState the new state of the timer
     */
    void onStateChanged(State newState);
}
