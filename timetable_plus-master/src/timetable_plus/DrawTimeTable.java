package timetable_plus;

import javax.swing.*;
import java.awt.*;

public class DrawTimeTable extends JPanel {
    // ... existing fields ...
    
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
