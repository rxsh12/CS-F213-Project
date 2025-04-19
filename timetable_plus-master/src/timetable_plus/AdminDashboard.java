package timetable_plus;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.IOException;

public class AdminDashboard extends JFrame {
    private InMemoryStore store = InMemoryStore.getInstance();
    private int currentScheduleIndex = 0;
    
    public AdminDashboard() {
        setTitle("Admin Dashboard - BITS Timetable");
        setSize(1024, 768);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // Create tabbed pane
        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Classrooms", createClassroomPanel());
        tabs.addTab("Courses", createCoursePanel());
        tabs.addTab("Instructors", createInstructorPanel());
        tabs.addTab("Generate Timetable", createSchedulePanel());
        
        add(tabs);
    }
    
    private JPanel createClassroomPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        // Table model for classrooms
        DefaultTableModel model = new DefaultTableModel(
            new Object[]{"Room No.", "Capacity", "Is Lab", "Has A/V Equipment"}, 0);
        
        // Populate table with classroom data
        for (Classroom room : store.getAllClassrooms().values()) {
            model.addRow(new Object[]{
                room.getRoomNumber(),
                room.getCapacity(),
                room.isLab() ? "Yes" : "No",
                room.hasAVEquipment() ? "Yes" : "No"
            });
        }
        
        JTable table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);
        
        // Add button panel for actions
        JPanel buttonPanel = new JPanel();
        JButton addButton = new JButton("Add Classroom");
        JButton importButton = new JButton("Import CSV");
        JButton exportButton = new JButton("Export CSV");
        
        addButton.addActionListener(this::showAddClassroomDialog);
        importButton.addActionListener(e -> importClassrooms());
        exportButton.addActionListener(e -> exportClassrooms());
        
        buttonPanel.add(addButton);
        buttonPanel.add(importButton);
        buttonPanel.add(exportButton);
        
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel createCoursePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        // Table model for courses
        DefaultTableModel model = new DefaultTableModel(
            new Object[]{"Course Code", "Course Name", "Credits", "Lectures", "Labs", "Students"}, 0);
        
        // Populate table with course data
        for (Course course : store.getAllCourses().values()) {
            model.addRow(new Object[]{
                course.getCourseCode(),
                course.getCourseName(),
                course.getCredits(),
                course.getLectureHours(),
                course.getLabHours(),
                course.getEnrolledStudents()
            });
        }
        
        JTable table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);
        
        // Add button panel for actions
        JPanel buttonPanel = new JPanel();
        JButton addButton = new JButton("Add Course");
        JButton importButton = new JButton("Import CSV");
        JButton exportButton = new JButton("Export CSV");
        
        addButton.addActionListener(this::showAddCourseDialog);
        importButton.addActionListener(e -> importCourses());
        exportButton.addActionListener(e -> exportCourses());
        
        buttonPanel.add(addButton);
        buttonPanel.add(importButton);
        buttonPanel.add(exportButton);
        
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel createInstructorPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        // Table model for instructors
        DefaultTableModel model = new DefaultTableModel(
            new Object[]{"ID", "Name", "Lectures", "Tutorials", "Labs"}, 0);
        
        // Populate table with instructor data
        for (Instructor instructor : store.getAllInstructors().values()) {
            model.addRow(new Object[]{
                instructor.getInstructorId(),
                instructor.getName(),
                instructor.getLecturesAssigned(),
                instructor.getTutorialsAssigned(),
                instructor.getLabsAssigned()
            });
        }
        
        JTable table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);
        
        // Add button panel for actions
        JPanel buttonPanel = new JPanel();
        JButton addButton = new JButton("Add Instructor");
        JButton importButton = new JButton("Import CSV");
        JButton exportButton = new JButton("Export CSV");
        
        addButton.addActionListener(this::showAddInstructorDialog);
        importButton.addActionListener(e -> importInstructors());
        exportButton.addActionListener(e -> exportInstructors());
        
        buttonPanel.add(addButton);
        buttonPanel.add(importButton);
        buttonPanel.add(exportButton);
        
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel createSchedulePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        // Add timetable view
        TimetablePanel timetablePanel = new TimetablePanel(store.getCurrentSchedule());
        panel.add(new JScrollPane(timetablePanel), BorderLayout.CENTER);
        
        // Add control panel
        JPanel controlPanel = new JPanel();
        JButton generateButton = new JButton("Auto-Generate");
        JButton saveButton = new JButton("Save Timetable");
        JButton prevButton = new JButton("« Previous");
        JButton nextButton = new JButton("Next »");
        
        generateButton.addActionListener(e -> generateNewTimetable());
        saveButton.addActionListener(e -> saveTimetable());
        prevButton.addActionListener(e -> showPreviousTimetable());
        nextButton.addActionListener(e -> showNextTimetable());
        
        controlPanel.add(generateButton);
        controlPanel.add(saveButton);
        controlPanel.add(prevButton);
        controlPanel.add(nextButton);
        
        panel.add(controlPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private void showAddClassroomDialog(ActionEvent e) {
        // Implementation for add classroom dialog
        JDialog dialog = new JDialog(this, "Add Classroom", true);
        // Dialog components and logic
        dialog.setVisible(true);
    }
    
    private void showAddCourseDialog(ActionEvent e) {
        // Implementation for add course dialog
        JDialog dialog = new JDialog(this, "Add Course", true);
        // Dialog components and logic
        dialog.setVisible(true);
    }
    
    private void showAddInstructorDialog(ActionEvent e) {
        // Implementation for add instructor dialog
        JDialog dialog = new JDialog(this, "Add Instructor", true);
        // Dialog components and logic
        dialog.setVisible(true);
    }
    
    private void importClassrooms() {
        JFileChooser fileChooser = new JFileChooser();
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                CSVHandler.importClassrooms(fileChooser.getSelectedFile().getPath());
                JOptionPane.showMessageDialog(this, "Classrooms imported successfully!");
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Error importing classrooms: " + ex.getMessage(),
                                            "Import Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void exportClassrooms() {
        JFileChooser fileChooser = new JFileChooser();
        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                CSVHandler.exportClassrooms(fileChooser.getSelectedFile().getPath());
                JOptionPane.showMessageDialog(this, "Classrooms exported successfully!");
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Error exporting classrooms: " + ex.getMessage(),
                                            "Export Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void importCourses() {
        JFileChooser fileChooser = new JFileChooser();
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                CSVHandler.importCourses(fileChooser.getSelectedFile().getPath());
                JOptionPane.showMessageDialog(this, "Courses imported successfully!");
                // Refresh the UI to show the newly imported courses
                refreshPanels();
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Error importing courses: " + ex.getMessage(),
                                            "Import Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void exportCourses() {
        JFileChooser fileChooser = new JFileChooser();
        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                CSVHandler.exportCourses(fileChooser.getSelectedFile().getPath());
                JOptionPane.showMessageDialog(this, "Courses exported successfully!");
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Error exporting courses: " + ex.getMessage(),
                                            "Export Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void importInstructors() {
        JFileChooser fileChooser = new JFileChooser();
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                CSVHandler.importInstructors(fileChooser.getSelectedFile().getPath());
                JOptionPane.showMessageDialog(this, "Instructors imported successfully!");
                // Refresh the UI to show the newly imported instructors
                refreshPanels();
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Error importing instructors: " + ex.getMessage(),
                                            "Import Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void exportInstructors() {
        JFileChooser fileChooser = new JFileChooser();
        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                CSVHandler.exportInstructors(fileChooser.getSelectedFile().getPath());
                JOptionPane.showMessageDialog(this, "Instructors exported successfully!");
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Error exporting instructors: " + ex.getMessage(),
                                            "Export Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    // Helper method to refresh all panels after import operations
    private void refreshPanels() {
        // This method would recreate or update the tabbed panels to reflect changes
        // For a complete implementation, you might want to refresh only the affected panel
        Container contentPane = getContentPane();
        contentPane.removeAll();
        
        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Classrooms", createClassroomPanel());
        tabs.addTab("Courses", createCoursePanel());
        tabs.addTab("Instructors", createInstructorPanel());
        tabs.addTab("Generate Timetable", createSchedulePanel());
        
        contentPane.add(tabs);
        contentPane.revalidate();
        contentPane.repaint();
    }
    
    private void generateNewTimetable() {
        try {
            BITSTimetableEngine engine = new BITSTimetableEngine();
            Schedule newSchedule = engine.generateSchedule();
            store.addSchedule(newSchedule);
            JOptionPane.showMessageDialog(this, "Timetable generated successfully!");
            // Refresh the timetable view
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error generating timetable: " + ex.getMessage(),
                                        "Generation Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void saveTimetable() {
        JFileChooser fileChooser = new JFileChooser();
        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            store.saveToFile(fileChooser.getSelectedFile().getPath());
            JOptionPane.showMessageDialog(this, "Timetable saved successfully!");
        }
    }
    
    private void showPreviousTimetable() {
        java.util.List<Schedule> schedules = store.getAllSchedules();
        if (schedules == null || schedules.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "No timetables available.", 
                "Navigation Error", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        currentScheduleIndex--;
        if (currentScheduleIndex < 0) {
            currentScheduleIndex = schedules.size() - 1;
        }

        Schedule schedule = schedules.get(currentScheduleIndex);
        store.setCurrentSchedule(schedule);
        refreshTimetablePanel();
    }

    private void showNextTimetable() {
        java.util.List<Schedule> schedules = store.getAllSchedules();
        if (schedules == null || schedules.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "No timetables available.", 
                "Navigation Error", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        currentScheduleIndex++;
        if (currentScheduleIndex >= schedules.size()) {
            currentScheduleIndex = 0;
        }

        Schedule schedule = schedules.get(currentScheduleIndex);
        store.setCurrentSchedule(schedule);
        refreshTimetablePanel();
    }

    private void refreshTimetablePanel() {
        Container contentPane = getContentPane();
        JTabbedPane tabs = (JTabbedPane) contentPane.getComponent(0);
        int scheduleTabIndex = tabs.indexOfTab("Generate Timetable");
        if (scheduleTabIndex != -1) {
            tabs.setComponentAt(scheduleTabIndex, createSchedulePanel());
            tabs.revalidate();
            tabs.repaint();
        }
    }
}
