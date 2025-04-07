package timetable_plus;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class LoginPage extends JFrame {
    private JComboBox<String> roleSelector;
    private JTextField usernameField;
    private JPasswordField passwordField;

    public LoginPage() {
        setTitle("BITS Timetable Login");
        setSize(350, 250);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        JPanel panel = new JPanel(new GridLayout(4, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        panel.add(new JLabel("Role:"));
        roleSelector = new JComboBox<>(new String[]{"Admin", "Faculty", "Student"});
        panel.add(roleSelector);
        
        panel.add(new JLabel("Username:"));
        usernameField = new JTextField();
        panel.add(usernameField);
        
        panel.add(new JLabel("Password:"));
        passwordField = new JPasswordField();
        panel.add(passwordField);
        
        JButton loginBtn = new JButton("Login");
        loginBtn.addActionListener(this::performLogin);
        panel.add(loginBtn);
        
        // Add a title label
        JLabel titleLabel = new JLabel("BITS Pilani Timetable System", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        
        // Main layout
        setLayout(new BorderLayout());
        add(titleLabel, BorderLayout.NORTH);
        add(panel, BorderLayout.CENTER);
    }

    private void performLogin(ActionEvent e) {
        String role = (String) roleSelector.getSelectedItem();
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());
        
        User user = InMemoryStore.getInstance().authenticate(username, password, role);
        if (user != null) {
            openDashboard(user);
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, 
                "Invalid credentials. Please try again.",
                "Login Failed", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void openDashboard(User user) {
        switch (user.getRole()) {
            case "Admin":
                new AdminDashboard().setVisible(true);
                break;
            case "Faculty":
                Faculty faculty = (Faculty) user;
                new FacultyPortal(faculty.getFacultyId()).setVisible(true);
                break;
            case "Student":
                Student student = (Student) user;
                new StudentPortal(student.getStudentId()).setVisible(true);
                break;
        }
    }
}
