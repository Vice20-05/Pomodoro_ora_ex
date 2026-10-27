package model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

/**
 * Unit tests for the PomodoroModel - no Swing / UI involved.
 */
public class PomodoroModelTest {

    private PomodoroModel createModel(int workSeconds, int shortBreakSeconds, int longBreakSeconds) {
        PomodoroModel model = new PomodoroModel();
        model.setDurations(workSeconds, shortBreakSeconds, longBreakSeconds);
        model.reset(); // reset the countdown to the new work duration
        return model;
    }

    @Test
    public void initialStateIsWorkWithFullTime() {
        PomodoroModel model = new PomodoroModel();
        assertEquals(State.WORK, model.getState());
        assertEquals(1500, model.getRemainingSeconds()); // 25 min default
        assertEquals(0, model.getCompletedRounds());
    }

    @Test
    public void tickDecrementsRemainingSeconds() {
        PomodoroModel model = createModel(10, 5, 15);
        model.start();
        model.tick();
        assertEquals(9, model.getRemainingSeconds());
        assertEquals(State.WORK, model.getState());
    }

    @Test
    public void finishingWorkStartsShortBreak() {
        PomodoroModel model = createModel(2, 1, 1);
        model.start();
        model.tick(); // 2 -> 1
        model.tick(); // finished -> short break
        assertEquals(State.SHORT_BREAK, model.getState());
        assertEquals(1, model.getCompletedRounds());
        assertEquals(1, model.getRemainingSeconds()); // short break duration
    }

    @Test
    public void longBreakStartsAfterFourthPomodoro() {
        PomodoroModel model = createModel(1, 1, 2);
        model.start();
        // Round 1
        model.tick(); // finished -> short break
        model.tick(); // break over -> work
        // Round 2
        model.tick(); // finished -> short break
        model.tick(); // break over -> work
        // Round 3
        model.tick(); // finished -> short break
        model.tick(); // break over -> work
        // Round 4
        model.tick(); // finished -> long break
        assertEquals(State.LONG_BREAK, model.getState());
        assertEquals(4, model.getCompletedRounds());
        assertEquals(2, model.getRemainingSeconds()); // long break duration
    }

    @Test
    public void resetRestoresInitialState() {
        PomodoroModel model = createModel(2, 1, 1);
        model.start();
        model.tick();
        model.reset();
        assertEquals(State.WORK, model.getState());
        assertEquals(2, model.getRemainingSeconds());
        assertEquals(0, model.getCompletedRounds());
    }

    @Test
    public void pausedModelDoesNotTick() {
        PomodoroModel model = new PomodoroModel(); // default durations
        model.start();
        model.pause();
        model.tick();
        assertEquals(State.PAUSED, model.getState());
        assertEquals(1500, model.getRemainingSeconds());
    }

    @Test
    public void observersAreNotifiedOnTickAndStateChange() {
        List<Integer> ticks = new ArrayList<>();
        List<State> states = new ArrayList<>();
        PomodoroModel model = createModel(3, 2, 5);
        model.subscribe(new PomodoroFigyelo() {
            @Override
            public void onTick(int remainingSeconds) {
                ticks.add(remainingSeconds);
            }

            @Override
            public void onStateChanged(State newState) {
                states.add(newState);
            }
        });

        model.start();
        model.tick();
        assertEquals(java.util.List.of(2), ticks);

        model.tick();
        model.tick(); // finished -> short break
        assertEquals(State.SHORT_BREAK, model.getState());
        assertEquals(java.util.List.of(2, 1, 2), ticks);
        assertEquals(java.util.List.of(State.SHORT_BREAK), states);
        assertTrue(states.contains(State.SHORT_BREAK));
    }
}
