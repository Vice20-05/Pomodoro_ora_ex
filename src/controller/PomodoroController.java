package controller;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;

import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import model.PomodoroModel;
import model.State;
import view.PomodoroView;

/**
 * Pomodoro controller.
 * <p>
 * Holds the ActionListeners of the view buttons and only delegates to the
 * model methods - the view never talks to the model directly.
 */
public class PomodoroController {

    /** The Swing Timer fires every second and delegates to the model. */
    private static final int TICK_MILLIS = 1000;

    private final PomodoroModel model;
    private final PomodoroView view;

    private final javax.swing.Timer timer;

    public PomodoroController(PomodoroModel model, PomodoroView view) {
        this.model = model;
        this.view = view;
        model.subscribe(view);

        view.getStartButton().addActionListener(e -> handleStart());
        view.getPauseButton().addActionListener(e -> handlePause());
        view.getResetButton().addActionListener(e -> handleReset());
        view.getSettingsButton().addActionListener(e -> openSettings());

        timer = new javax.swing.Timer(TICK_MILLIS, e -> model.tick());
        timer.setInitialDelay(TICK_MILLIS);
        timer.setRepeats(true);
    }

    /** Button handler: starts / resumes the countdown. */
    public void handleStart() {
        model.start();
        if (model.getState() != State.PAUSED) {
            startTimer();
        }
    }

    /** Button handler: pauses the countdown. */
    public void handlePause() {
        model.pause();
        stopTimer();
    }

    /** Button handler: resets the countdown. */
    public void handleReset() {
        stopTimer();
        model.reset();
    }

    /** Opens the modal settings dialog for the durations. */
    public void openSettings() {
        JDialog dialog = new JDialog(view, "Pomodoro Settings", true);
        dialog.setSize(440, 240);
        dialog.setLocationRelativeTo(view);

        JPanel settingsPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        settingsPanel.add(new javax.swing.JLabel("Work minutes:"));
        JTextField workField = new JTextField("25");
        settingsPanel.add(workField);

        settingsPanel.add(new javax.swing.JLabel("Short break minutes:"));
        JTextField shortBreakField = new JTextField("5");
        settingsPanel.add(shortBreakField);

        settingsPanel.add(new javax.swing.JLabel("Long break minutes:"));
        JTextField longBreakField = new JTextField("15");
        settingsPanel.add(longBreakField);

        JPanel dialogButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        javax.swing.JButton okButton = new javax.swing.JButton("OK");
        javax.swing.JButton cancelButton = new javax.swing.JButton("Cancel");
        dialogButtonPanel.add(okButton);
        dialogButtonPanel.add(cancelButton);

        JPanel contentPanel = new JPanel(new BorderLayout(10, 10));
        contentPanel.add(settingsPanel, BorderLayout.CENTER);
        contentPanel.add(dialogButtonPanel, BorderLayout.SOUTH);
        contentPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(12, 12, 12, 12));
        dialog.setContentPane(contentPanel);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        okButton.addActionListener(e -> {
            try {
                int workMinutes = Integer.parseInt(workField.getText().trim());
                int shortBreakMinutes = Integer.parseInt(shortBreakField.getText().trim());
                int longBreakMinutes = Integer.parseInt(longBreakField.getText().trim());
                if (workMinutes <= 0 || shortBreakMinutes <= 0 || longBreakMinutes <= 0) {
                    throw new NumberFormatException("Durations must be positive.");
                }
                model.setDurations(workMinutes * 60, shortBreakMinutes * 60, longBreakMinutes * 60);
                dialog.dispose();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(view, "Please enter positive numbers.", "Invalid duration", JOptionPane.ERROR_MESSAGE);
            }
        });
        cancelButton.addActionListener(e -> dialog.dispose());

        dialog.setVisible(true);
    }

    /** Starts the Swing Timer that drives the model ticks. */
    private void startTimer() {
        if (!timer.isRunning()) {
            timer.start();
        }
    }

    /** Stops the Swing Timer (e.g. on pause / reset). */
    private void stopTimer() {
        timer.stop();
    }
}
