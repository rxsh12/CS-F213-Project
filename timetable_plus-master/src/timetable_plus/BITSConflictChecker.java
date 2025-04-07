package timetable_plus;

import java.util.List;

public class BITSConflictChecker {
    public static boolean hasRoomConflict(String roomId, int day, int period, Schedule schedule) {
        List<ScheduleSlot> roomAllocations = schedule.getRoomAllocations(roomId);
        
        for (ScheduleSlot slot : roomAllocations) {
            if (slot.getDay() == day && slot.overlaps(period)) {
                return true;
            }
        }
        return false;
    }
    
    public static boolean hasInstructorConflict(String instructorId, int day, int period, Schedule schedule) {
        List<ScheduleSlot> instructorAllocations = schedule.getInstructorAllocations(instructorId);
        
        for (ScheduleSlot slot : instructorAllocations) {
            if (slot.getDay() == day && slot.overlaps(period)) {
                return true;
            }
        }
        return false;
    }
    
    public static boolean hasCourseConflict(Course course, int day, Schedule schedule) {
        List<ScheduleSlot> courseAllocations = schedule.getCourseAllocations(course);
        
        for (ScheduleSlot slot : courseAllocations) {
            if (Math.abs(slot.getDay() - day) <= BITSConstraints.LECTURE_GAP_DAYS) {
                return true;
            }
        }
        return false;
    }
    
    public static boolean isValidAllocation(Course course, String roomId, String instructorId, 
                                         int day, int period, Schedule schedule) {
        // Check all constraints
        return BITSConstraints.isValidLunchBreak(period) && 
               !hasRoomConflict(roomId, day, period, schedule) &&
               !hasInstructorConflict(instructorId, day, period, schedule) &&
               (course.getLabHours() <= 0 || period + BITSConstraints.LAB_DURATION <= 
                InMemoryStore.PERIODS_PER_DAY);
    }

    public static boolean hasTimeConflict(Schedule schedule, int day, int period) {
        for (ScheduleSlot slot : schedule.getSlots()[day]) {
            if (slot.getPeriod() == period && !slot.isAvailable()) {
                return true;
            }
        }
        return false;
    }
    
}
