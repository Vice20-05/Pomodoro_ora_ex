package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Toolkit;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import model.PomodoroFigyelo;
import model.PomodoroModel;
import model.State;

/**
 * Pomodoro timer Swing view.
 * <p>
 * Implements the {@link PomodoroFigyelo} observer interface, so the model can
 * push its updates (tick + state change) into the UI. All UI updates are done
 * here, the logic itself lives in the model.
 */
public class PomodoroView extends JFrame implements PomodoroFigyelo {

    private static final long serialVersionUID = 1L;

    private JLabel timeLabel;
    private JLabel statusLabel;
    private JButton startButton;
    private JButton pauseButton;
    private JButton resetButton;
    private JButton settingsButton;
    private JPanel mainPanel;

    public PomodoroView() {
        setTitle("Pomodoro Timer");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.WHITE);

        statusLabel = new JLabel(statusText(State.WORK), SwingConstants.CENTER);
        timeLabel = new JLabel(formatTime(PomodoroModel.DEFAULT_WORK_SECONDS), SwingConstants.CENTER);
        timeLabel.setFont(new Font("Dialog", Font.BOLD, 72));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        startButton = new JButton("Start");
        pauseButton = new JButton("Pause");
        resetButton = new JButton("Reset");
        settingsButton = new JButton("Settings");
        buttonPanel.add(startButton);
        buttonPanel.add(pauseButton);
        buttonPanel.add(resetButton);
        buttonPanel.add(settingsButton);

        mainPanel.add(statusLabel, BorderLayout.NORTH);
        mainPanel.add(timeLabel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        setContentPane(mainPanel);
        setSize(420, 360);
        setLocationRelativeTo(null);

        // Initial display - will be overwritten by the first observer notification.
        setStatusAndColor(State.WORK);
        onTick(PomodoroModel.DEFAULT_WORK_SECONDS);
    }

    /**
     * Observer callback: the countdown changed -> update the MM:SS label.
     */
    @Override
    public void onTick(int remainingSeconds) {
        timeLabel.setText(formatTime(remainingSeconds));
    }

    /**
     * Observer callback: the state changed -> update the status/colors and beep.
     */
    @Override
    public void onStateChanged(State newState) {
        setStatusAndColor(newState);
        Toolkit.getDefaultToolkit().beep();
    }

    /** Updates the status label and the countdown color for the given state. */
    private void setStatusAndColor(State newState) {
        statusLabel.setText(statusText(newState));
        Color color = colorFor(newState);
        timeLabel.setForeground(color);
        statusLabel.setForeground(color);
    }

    /** Human readable status text for the state. */
    private String statusText(State state) {
        switch (state) {
            case WORK:
                return "Work";
            case SHORT_BREAK:
                return "Short Break";
            case LONG_BREAK:
                return "Long Break";
            case PAUSED:
                return "Paused";
            default:
                return "Work";
        }
    }

    /** Background color of the countdown for each state. */
    private Color colorFor(State state) {
        switch (state) {
            case WORK:
                return new Color(0xE53935);       // red
            case SHORT_BREAK:
                return new Color(0x43A047);       // green
            case LONG_BREAK:
                return new Color(0x8E24AA);       // purple
            case PAUSED:
                return new Color(0x757575);       // gray
            default:
                return Color.GRAY;
        }
    }

    /** Formats seconds into MM:SS. */
    private String formatTime(int seconds) {
        int minutes = seconds / 60;
        int rest = seconds % 60;
        return String.format("%02d:%02d", minutes, rest);
    }

    public JButton getStartButton() {
        return startButton;
    }

    public JButton getPauseButton() {
        return pauseButton;
    }

    public JButton getResetButton() {
        return resetButton;
    }

    public JButton getSettingsButton() {
        return settingsButton;
    }
}
