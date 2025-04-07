package timetable_plus;

import timetable_plus.exceptions.*;

public class BITSConstraints {
    // BITS academic constants
    public static final int MAX_CREDITS = 25;
    public static final int MAX_LECTURES_PER_FACULTY = 1;
    public static final int MAX_TUTORIALS_PER_FACULTY = 2;
    public static final int MAX_LABS_PER_FACULTY = 1;
    public static final int LUNCH_START = 4; // 12 PM
    public static final int LUNCH_END = 6;   // 2 PM
    public static final int LAB_DURATION = 2; // 2-hour labs
    public static final int LECTURE_GAP_DAYS = 1; // Gap between consecutive lectures
    
    // Validation methods
    public static boolean isValidLunchBreak(int period) {
        return period < LUNCH_START || period >= LUNCH_END;
    }
    
    public static void validateLunchBreak(int period) throws LunchBreakException {
        if (!isValidLunchBreak(period)) {
            throw new LunchBreakException();
        }
    }
    
    public static void validateLabDuration(Course course, int duration) throws LabDurationException {
        if (course.getLabHours() > 0 && duration != LAB_DURATION) {
            throw new LabDurationException(duration);
        }
    }
    
    public static void validateLectureSpacing(Schedule schedule, Course course, int newDay) 
            throws ConsecutiveLectureException {
        for (ScheduleSlot slot : schedule.getCourseAllocations(course)) {
            if (Math.abs(slot.getDay() - newDay) <= LECTURE_GAP_DAYS) {
                throw new ConsecutiveLectureException(course.getCourseCode(), 
                                                    slot.getDay(), newDay);
            }
        }
    }
    
    public static void validateFacultyWorkload(String facultyId, String assignmentType, 
                                             InMemoryStore store) 
            throws FacultyWorkloadException {
        int lectureCount = store.getFacultyLectureCount(facultyId);
        int tutorialCount = store.getFacultyTutorialCount(facultyId);
        int labCount = store.getFacultyLabCount(facultyId);
        
        switch (assignmentType) {
            case "lecture":
                if (lectureCount >= MAX_LECTURES_PER_FACULTY)
                    throw new FacultyWorkloadException(facultyId, "lecture");
                break;
            case "tutorial":
                if (tutorialCount >= MAX_TUTORIALS_PER_FACULTY)
                    throw new FacultyWorkloadException(facultyId, "tutorial");
                break;
            case "lab":
                if (labCount >= MAX_LABS_PER_FACULTY)
                    throw new FacultyWorkloadException(facultyId, "lab");
                break;
        }
    }
    
    public static void validateCreditLimit(String studentId, int currentCredits, int newCourseCredits) 
            throws CreditLimitException {
        if (currentCredits + newCourseCredits > MAX_CREDITS) {
            throw new CreditLimitException(studentId, currentCredits, newCourseCredits);
        }
    }
    
    public static void validateRoomCapacity(Classroom room, Course course) 
            throws RoomCapacityException {
        if (room.getCapacity() < course.getEnrolledStudents()) {
            throw new RoomCapacityException(room.getRoomNumber(), 
                                          room.getCapacity(), 
                                          course.getEnrolledStudents());
        }
    }
}
