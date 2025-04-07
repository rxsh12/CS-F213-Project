package timetable_plus;

import javax.swing.*;
import java.awt.*;

public class TimetablePanel extends JPanel {
    private static final String[] DAYS = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday"};
    private static final String[] PERIODS = {"8-9", "9-10", "10-11", "11-12", "12-1", "1-2", "2-3", "3-4", "4-5"};
    
    private Schedule schedule;
    
    public TimetablePanel(Schedule schedule) {
        this.schedule = schedule;
        setLayout(new BorderLayout());
        initTimetable();
    }
    
    private void initTimetable() {
        // Create grid layout for timetable
        JPanel timetableGrid = new JPanel(new GridLayout(DAYS.length + 1, PERIODS.length + 1));
        
        // Add empty corner cell
        timetableGrid.add(new JLabel());
        
        // Add period headers
        for (String period : PERIODS) {
            JLabel label = new JLabel(period, JLabel.CENTER);
            label.setBorder(BorderFactory.createLineBorder(Color.BLACK));
            timetableGrid.add(label);
        }
        
        // Add day rows with slots
        for (int day = 0; day < DAYS.length; day++) {
            // Add day label
            JLabel dayLabel = new JLabel(DAYS[day], JLabel.CENTER);
            dayLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
            timetableGrid.add(dayLabel);
            
            // Add slots for each period
            for (int period = 0; period < PERIODS.length; period++) {
                timetableGrid.add(createSlotPanel(day, period));
            }
        }
        
        add(timetableGrid, BorderLayout.CENTER);
    }
    
    private JPanel createSlotPanel(int day, int period) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        
        // Get the schedule slot
        ScheduleSlot slot = schedule.getSlot(day, period);
        
        // Skip lunch time slots
        if (period >= BITSConstraints.LUNCH_START && period < BITSConstraints.LUNCH_END) {
            panel.setBackground(new Color(255, 222, 173)); // Light orange
            JLabel lunchLabel = new JLabel("LUNCH", JLabel.CENTER);
            panel.add(lunchLabel, BorderLayout.CENTER);
            return panel;
        }
        
        // Render course information if allocated
        if (slot != null && slot.getCourse() != null) {
            Course course = slot.getCourse();
            panel.setBackground(course.getDisplayColor());
            
            JLabel courseLabel = new JLabel(course.getCourseCode(), JLabel.CENTER);
            JLabel roomLabel = new JLabel(slot.getRoomNumber(), JLabel.CENTER);
            roomLabel.setFont(new Font("Arial", Font.PLAIN, 10));
            
            panel.add(courseLabel, BorderLayout.CENTER);
            panel.add(roomLabel, BorderLayout.SOUTH);
        } else {
            panel.setBackground(Color.WHITE);
        }
        
        return panel;
    }
}
