package timetable_plus;

import java.io.Serializable;

public class ScheduleSlot implements Serializable {
    private Course course;
    private String roomNumber;
    private String instructorId;
    private boolean locked;
    private int day;
    private int period;
    
    public ScheduleSlot() {
        this.locked = false;
    }
    
    public ScheduleSlot(int day, int period) {
        this.day = day;
        this.period = period;
        this.locked = false;
    }
    
    // BITS-specific methods
    public boolean isAvailable() {
        return !locked && course == null;
    }
    
    public void allocate(Course course, String room, String instructor) {
        if (!locked) {
            this.course = course;
            this.roomNumber = room;
            this.instructorId = instructor;
        }
    }
    
    public boolean overlaps(int otherPeriod) {
        if (course == null) return false;
        
        int duration = 1;
        if (course.getLabHours() > 0) duration = 2;
        
        return (period <= otherPeriod && otherPeriod < period + duration);
    }
    
    // Getters and setters
    public Course getCourse() { return course; }
    public void setCourse(Course course) { this.course = course; }
    
    public String getRoomNumber() { return roomNumber; }
    public void setRoomNumber(String room) { this.roomNumber = room; }
    
    public String getInstructorId() { return instructorId; }
    public void setInstructorId(String instructor) { this.instructorId = instructor; }
    
    public boolean isLocked() { return locked; }
    public void setLocked(boolean locked) { this.locked = locked; }
    
    public int getDay() { return day; }
    public void setDay(int day) { this.day = day; }
    
    public int getPeriod() { return period; }
    public void setPeriod(int period) { this.period = period; }
}
