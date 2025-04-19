package timetable_plus;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Schedule implements Serializable {
    private String name;
    private ScheduleSlot[][] slots;
    
    public Schedule(String name) {
        this.name = name;
        this.slots = new ScheduleSlot[InMemoryStore.WORKING_DAYS][InMemoryStore.PERIODS_PER_DAY];
    }
    
    public void setSlot(int day, int period, ScheduleSlot slot) {
        if (day < 0 || day >= InMemoryStore.WORKING_DAYS || 
            period < 0 || period >= InMemoryStore.PERIODS_PER_DAY) {
            throw new IllegalArgumentException("Invalid day or period");
        }
        slots[day][period] = slot;
    }
    
    public ScheduleSlot getSlot(int day, int period) {
        if (day < 0 || day >= InMemoryStore.WORKING_DAYS || 
            period < 0 || period >= InMemoryStore.PERIODS_PER_DAY) {
            throw new IllegalArgumentException("Invalid day or period");
        }
        return slots[day][period];
    }
    
    public List<ScheduleSlot> getCourseAllocations(Course course) {
        List<ScheduleSlot> allocations = new ArrayList<>();
        
        for(int day = 0; day < InMemoryStore.WORKING_DAYS; day++) {
            for(int period = 0; period < InMemoryStore.PERIODS_PER_DAY; period++) {
                if(slots[day][period].getCourse() != null &&
                   slots[day][period].getCourse().getCourseCode().equals(course.getCourseCode())) {
                    allocations.add(slots[day][period]);
                }
            }
        }
        
        return allocations;
    }
    
    public List<ScheduleSlot> getRoomAllocations(String roomId) {
        List<ScheduleSlot> allocations = new ArrayList<>();
        
        for(int day = 0; day < InMemoryStore.WORKING_DAYS; day++) {
            for(int period = 0; period < InMemoryStore.PERIODS_PER_DAY; period++) {
                if(slots[day][period].getRoomNumber() != null &&
                   slots[day][period].getRoomNumber().equals(roomId)) {
                    allocations.add(slots[day][period]);
                }
            }
        }
        
        return allocations;
    }
    
    public List<ScheduleSlot> getInstructorAllocations(String instructorId) {
        List<ScheduleSlot> allocations = new ArrayList<>();
        
        for(int day = 0; day < InMemoryStore.WORKING_DAYS; day++) {
            for(int period = 0; period < InMemoryStore.PERIODS_PER_DAY; period++) {
                if(slots[day][period].getInstructorId() != null &&
                   slots[day][period].getInstructorId().equals(instructorId)) {
                    allocations.add(slots[day][period]);
                }
            }
        }
        
        return allocations;
    }
    
    public ScheduleSlot[][] getSlots() {
        return slots;
    }
    
    public String getName() {
        return name;
    }
}
