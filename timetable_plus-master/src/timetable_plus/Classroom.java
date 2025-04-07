package timetable_plus;

import java.io.Serializable;

public class Classroom implements Serializable {
    private String roomNumber;
    private int capacity;
    private boolean isLab;
    private boolean hasAVEquipment;
    private int computerCount;
    private boolean hasWorkbenches;
    
    public Classroom(String number, int capacity) {
        this.roomNumber = number;
        this.capacity = capacity;
        
        // Initialize based on BITS nomenclature
        this.isLab = number.startsWith("D") || number.startsWith("I0");
        this.hasAVEquipment = number.startsWith("F");
    }
    
    // Builder pattern methods
    public Classroom withAV(boolean hasAV) {
        this.hasAVEquipment = hasAV;
        return this;
    }
    
    public Classroom withComputers(int count) {
        this.computerCount = count;
        return this;
    }
    
    public Classroom withWorkbenches(boolean hasWorkbenches) {
        this.hasWorkbenches = hasWorkbenches;
        return this;
    }
    
    public boolean canHostCourse(Course course) {
        return capacity >= course.getEnrolledStudents() &&
               (course.getLabHours() == 0 || isLab);
    }
    
    // Getters
    public String getRoomNumber() { return roomNumber; }
    public int getCapacity() { return capacity; }
    public boolean isLab() { return isLab; }
    public boolean hasAVEquipment() { return hasAVEquipment; }
    public int getComputerCount() { return computerCount; }
    public boolean hasWorkbenches() { return hasWorkbenches; }
    
    @Override
    public String toString() {
        return roomNumber + " (Capacity: " + capacity + (isLab ? ", Lab)" : ")");
    }
}
