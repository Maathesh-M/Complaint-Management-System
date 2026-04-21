package main;
import ui.LoginFrame;
import javax.swing.*;
public class MainApp {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                System.err.println("[Main] Could not set system L&F: " + e.getMessage());
            }
            new LoginFrame().setVisible(true);
        });
    }
}