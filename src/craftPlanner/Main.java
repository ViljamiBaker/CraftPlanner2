package craftPlanner;

import javax.swing.SwingUtilities;

import craftPlanner.GUI.MainFrame;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new MainFrame();
            }
        });
    }
}
