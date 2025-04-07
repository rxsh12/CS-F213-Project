package timetable_plus;

import javax.swing.*;
import java.awt.*;

public class FacultyPortal extends JFrame {
    private String facultyId;
    private InMemoryStore store = InMemoryStore.getInstance();
    
    public FacultyPortal(String facultyId) {
        this.facultyId = facultyId;
        Instructor instructor = store.getInstructor(facultyId);
        
        setTitle("Faculty Portal - " + instructor.getName());
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        initUI();
    }
    
    private void initUI() {
        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("My Schedule", createSchedulePanel());
        tabs.addTab("Course Load", createWorkloadPanel());
        
        add(tabs);
    }
    
    private JPanel createSchedulePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        // Get the faculty-specific timetable
        Schedule facultySchedule = store.getFacultySchedule(facultyId);
        TimetablePanel timetablePanel = new TimetablePanel(facultySchedule);
        
        panel.add(new JScrollPane(timetablePanel), BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createWorkloadPanel() {
        JPanel panel = new JPanel(new GridLayout(4, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        Instructor instructor = store.getInstructor(facultyId);
        
        panel.add(new JLabel("Instructor ID:"));
        panel.add(new JLabel(instructor.getInstructorId()));
        
        panel.add(new JLabel("Lectures Assigned:"));
        panel.add(new JLabel(String.valueOf(instructor.getLecturesAssigned())));
        
        panel.add(new JLabel("Tutorials Assigned:"));
        panel.add(new JLabel(String.valueOf(instructor.getTutorialsAssigned())));
        
        panel.add(new JLabel("Labs Assigned:"));
        panel.add(new JLabel(String.valueOf(instructor.getLabsAssigned())));
        
        return panel;
    }
}
