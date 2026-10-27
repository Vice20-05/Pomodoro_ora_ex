import javax.swing.SwingUtilities;

import controller.PomodoroController;
import model.PomodoroModel;
import view.PomodoroView;

/**
 * Application entry point - wires the MVC components together
 * and shows the window on the Event Dispatch Thread.
 */
public class Main {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            PomodoroModel model = new PomodoroModel();
            PomodoroView view = new PomodoroView();
            new PomodoroController(model, view);
            view.setVisible(true);
        });
    }
}
