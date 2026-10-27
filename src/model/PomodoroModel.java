package model;

import java.util.ArrayList;
import java.util.List;

/**
 * Pomodoro countdown model.
 * <p>
 * Stores the current state, the remaining seconds and the number of finished
 * pomodoros. The class has no Swing dependency - the view (Swing UI) implements
 * the {@link PomodoroFigyelo} observer interface and gets notified on every tick
 * and on every state change.
 */
public class PomodoroModel {

    /** Default work time: 25 minutes. */
    public static final int DEFAULT_WORK_SECONDS = 25 * 60;

    /** Default short break time: 5 minutes. */
    public static final int DEFAULT_SHORT_BREAK_SECONDS = 5 * 60;

    /** Default long break time after four pomodoros: 15 minutes. */
    public static final int DEFAULT_LONG_BREAK_SECONDS = 15 * 60;

    private State state = State.WORK;

    private int remainingSeconds;

    private int completedRounds;

    private int workSeconds = DEFAULT_WORK_SECONDS;

    private int shortBreakSeconds = DEFAULT_SHORT_BREAK_SECONDS;

    private int longBreakSeconds = DEFAULT_LONG_BREAK_SECONDS;

    private State stateBeforePause = State.WORK;

    private final List<PomodoroFigyelo> observers = new ArrayList<>();

    public PomodoroModel() {
        remainingSeconds = workSeconds;
    }

    // ------------------------------------------------------------------
    // Observer management (Observer pattern)
    // ------------------------------------------------------------------

    /** Registers a new observer. */
    public void subscribe(PomodoroFigyelo observer) {
        if (observer == null) {
            return;
        }
        if (!observers.contains(observer)) {
            observers.add(observer);
        }
    }

    /** Removes an observer. */
    public void unsubscribe(PomodoroFigyelo observer) {
        observers.remove(observer);
    }

    private void notifyTick(int remainingSeconds) {
        for (PomodoroFigyelo observer : observers) {
            observer.onTick(remainingSeconds);
        }
    }

    private void notifyStateChanged(State newState) {
        for (PomodoroFigyelo observer : observers) {
            observer.onStateChanged(newState);
        }
    }

    // ------------------------------------------------------------------
    // Commands
    // ------------------------------------------------------------------

    /** Starts / resumes the timer. */
    public void start() {
        if (state == State.PAUSED) {
            state = stateBeforePause;
            notifyStateChanged(state);
        }
    }

    /** Pauses the timer. */
    public void pause() {
        if (state == State.PAUSED) {
            return;
        }
        stateBeforePause = state;
        state = State.PAUSED;
        notifyStateChanged(State.PAUSED);
    }

    /** Resets the timer to the initial work state. */
    public void reset() {
        state = State.WORK;
        stateBeforePause = State.WORK;
        remainingSeconds = workSeconds;
        completedRounds = 0;
        notifyStateChanged(State.WORK);
        notifyTick(remainingSeconds);
    }

    /**
     * Called every second by the Swing Timer.
     * Decrements the countdown and handles the phase transitions.
     */
    public void tick() {
        if (state == State.PAUSED) {
            return;
        }

        if (state == State.WORK) {
            remainingSeconds--;
            if (remainingSeconds > 0) {
                notifyTick(remainingSeconds);
                return;
            }

            // A pomodoro finished -> start a break.
            completedRounds++;
            State nextState = completedRounds % 4 == 0 ? State.LONG_BREAK : State.SHORT_BREAK;
            int nextDuration = nextState == State.LONG_BREAK ? longBreakSeconds : shortBreakSeconds;
            enterState(nextState, nextDuration);
            return;
        }

        // We are in a break -> the next work interval begins when it ends.
        remainingSeconds--;
        if (remainingSeconds > 0) {
            notifyTick(remainingSeconds);
            return;
        }

        if (state == State.LONG_BREAK) {
            // Long break finished -> a new cycle starts.
            completedRounds = 0;
        }
        enterState(State.WORK, workSeconds);
    }

    private void enterState(State newState, int durationSeconds) {
        state = newState;
        remainingSeconds = durationSeconds;
        notifyStateChanged(newState);
        notifyTick(remainingSeconds);
    }

    /** Updates the configurable durations (in seconds). */
    public void setDurations(int workSeconds, int shortBreakSeconds, int longBreakSeconds) {
        this.workSeconds = workSeconds;
        this.shortBreakSeconds = shortBreakSeconds;
        this.longBreakSeconds = longBreakSeconds;
    }

    // ------------------------------------------------------------------
    // Getters (state inspection / tests)
    // ------------------------------------------------------------------

    public State getState() {
        return state;
    }

    public int getRemainingSeconds() {
        return remainingSeconds;
    }

    public int getCompletedRounds() {
        return completedRounds;
    }

    public int getWorkSeconds() {
        return workSeconds;
    }

    public int getShortBreakSeconds() {
        return shortBreakSeconds;
    }

    public int getLongBreakSeconds() {
        return longBreakSeconds;
    }
}
