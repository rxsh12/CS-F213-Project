package timetable_plus;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class DrawTimeTable extends JPanel {
    // ... existing fields ...
    
    private Map<Integer, Integer> labSessions;

    private void initializeLabSessions() {
        labSessions = new HashMap<>();
        // Example: Lab on Day 2 (Wednesday) starting at Period 3
        labSessions.put(2, 3); // Day 2, Periods 3 and 4 are lab sessions
    }

    private boolean isLabSession(int day, int period) {
        if (labSessions.containsKey(day)) {
            int startPeriod = labSessions.get(day);
            return period == startPeriod || period == startPeriod + 1; // Two consecutive periods
        }
        return false;
    }
    
    private void drawBITSFeatures(Graphics gc) {
        // Highlight lunch breaks
        for(int day=0; day<5; day++) {
            for(int period=4; period<6; period++) {
                gc.setColor(new Color(255, 223, 186));
                gc.fillRect(period*50+50, day*50+30, 100, 50);
                gc.setColor(Color.RED);
                gc.drawString("LUNCH", period*50+60, day*50+55);
            }
        }
        
        // Highlight labs
        for(int day=0; day<5; day++) {
            for(int period=0; period<9; period++) {
                if(isLabSession(day, period)) {
                    gc.setColor(new Color(173, 216, 230));
                    gc.fillRect(period*50+50, day*50+30, 100, 50);
                }
            }
        }
    }
}
