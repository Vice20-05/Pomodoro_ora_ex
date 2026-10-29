package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Toolkit;

import javax.swing.BorderFactory;
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

        // dark background with a neon frame around the whole window
        mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(CyberpunkTheme.BACKGROUND);
        mainPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(CyberpunkTheme.MAGENTA, 2),
                BorderFactory.createEmptyBorder(14, 14, 14, 14)));

        statusLabel = new JLabel(statusText(State.WORK), SwingConstants.CENTER);
        statusLabel.setFont(new Font(Font.MONOSPACED, Font.BOLD, 20));
        timeLabel = new JLabel(formatTime(PomodoroModel.DEFAULT_WORK_SECONDS), SwingConstants.CENTER);
        timeLabel.setFont(new Font(Font.MONOSPACED, Font.BOLD, 84));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 0));
        buttonPanel.setOpaque(false);
        startButton = new JButton("START");
        pauseButton = new JButton("PAUSE");
        resetButton = new JButton("RESET");
        settingsButton = new JButton("CONFIG");
        CyberpunkTheme.neonButton(startButton, CyberpunkTheme.CYAN);
        CyberpunkTheme.neonButton(pauseButton, CyberpunkTheme.YELLOW);
        CyberpunkTheme.neonButton(resetButton, CyberpunkTheme.MAGENTA);
        CyberpunkTheme.neonButton(settingsButton, CyberpunkTheme.PURPLE);
        buttonPanel.add(startButton);
        buttonPanel.add(pauseButton);
        buttonPanel.add(resetButton);
        buttonPanel.add(settingsButton);

        mainPanel.add(statusLabel, BorderLayout.NORTH);
        mainPanel.add(timeLabel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        setContentPane(mainPanel);
        setSize(520, 360);
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
                return ">> WORK_MODE";
            case SHORT_BREAK:
                return ">> SHORT_BREAK";
            case LONG_BREAK:
                return ">> LONG_BREAK";
            case PAUSED:
                return ">> PAUSED //";
            default:
                return ">> WORK_MODE";
        }
    }

    /** Neon color of the countdown for each state. */
    private Color colorFor(State state) {
        switch (state) {
            case WORK:
                return CyberpunkTheme.MAGENTA;
            case SHORT_BREAK:
                return CyberpunkTheme.CYAN;
            case LONG_BREAK:
                return CyberpunkTheme.PURPLE;
            case PAUSED:
                return CyberpunkTheme.DIM;
            default:
                return CyberpunkTheme.DIM;
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
