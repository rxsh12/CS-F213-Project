package timetable_plus;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        try {
            DatabaseManager.initialize();
            InMemoryStore.getInstance().loadInitialData();
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            
            SwingUtilities.invokeLater(() -> {
                LoginPage loginPage = new LoginPage();
                loginPage.setVisible(true);
            });
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, 
                "Failed to start application: " + e.getMessage(),
                "Startup Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
