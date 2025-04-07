package timetable_plus;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import timetable_plus.exceptions.*;

public class StudentPortal extends JFrame {
    private String studentId;
    private InMemoryStore store = InMemoryStore.getInstance();
    private List<Course> selectedCourses = new ArrayList<>();
    
    public StudentPortal(String studentId) {
        this.studentId = studentId;
        
        setTitle("Student Portal - " + studentId);
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        initUI();
    }
    
    private void initUI() {
        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Course Enrollment", createEnrollmentPanel());
        tabs.addTab("My Schedule", createSchedulePanel());
        
        add(tabs);
    }
    
    private JPanel createEnrollmentPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        // Create table model for available courses
        DefaultTableModel model = new DefaultTableModel(
            new Object[]{"Select", "Course Code", "Course Name", "Credits", "Enrolled"}, 0) {
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return columnIndex == 0 ? Boolean.class : String.class;
            }
            
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 0; // Only checkbox column is editable
            }
        };
        
        // Populate table with course data
        for (Course course : store.getAllCourses().values()) {
            model.addRow(new Object[]{
                false, // Checkbox
                course.getCourseCode(),
                course.getCourseName(),
                course.getCredits(),
                course.getEnrolledStudents()
            });
        }
        
        JTable table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);
        
        // Add control panel
        JPanel controlPanel = new JPanel();
        JButton enrollButton = new JButton("Enroll in Selected Courses");
        JLabel creditLabel = new JLabel("Current Credits: 0/" + BITSConstraints.MAX_CREDITS);
        
        enrollButton.addActionListener(e -> enrollInSelectedCourses(table));
        
        controlPanel.add(creditLabel);
        controlPanel.add(enrollButton);
        
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(controlPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel createSchedulePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        // Get the student-specific timetable
        Schedule studentSchedule = store.getStudentSchedule(studentId);
        TimetablePanel timetablePanel = new TimetablePanel(studentSchedule);
        
        panel.add(new JScrollPane(timetablePanel), BorderLayout.CENTER);
        
        return panel;
    }
    
    private void enrollInSelectedCourses(JTable table) {
        selectedCourses.clear();
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        
        // Collect selected courses
        for (int i = 0; i < model.getRowCount(); i++) {
            boolean selected = (boolean) model.getValueAt(i, 0);
            if (selected) {
                String courseCode = (String) model.getValueAt(i, 1);
                Course course = store.getCourse(courseCode);
                if (course != null) {
                    selectedCourses.add(course);
                }
            }
        }
        
        // Validate enrollment
        try {
            // Find the student
            Student student = null;
            for (User user : store.getAllUsers()) {
                if (user instanceof Student && ((Student)user).getStudentId().equals(studentId)) {
                    student = (Student) user;
                    break;
                }
            }
            
            if (student == null) {
                throw new Exception("Student not found");
            }
            
            // Calculate total credits
            int totalCredits = student.getCurrentCredits();
            for (Course course : selectedCourses) {
                BITSConstraints.validateCreditLimit(studentId, totalCredits, course.getCredits());
                totalCredits += course.getCredits();
            }
            
            // Successful enrollment
            JOptionPane.showMessageDialog(this, 
                "Successfully enrolled in " + selectedCourses.size() + " courses.",
                "Enrollment Successful", JOptionPane.INFORMATION_MESSAGE);
            
        } catch (CreditLimitException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(),
                                        "Credit Limit Exceeded", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Enrollment failed: " + e.getMessage(),
                                        "Enrollment Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
