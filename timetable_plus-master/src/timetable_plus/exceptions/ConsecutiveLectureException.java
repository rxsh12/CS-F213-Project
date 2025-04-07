package timetable_plus.exceptions;

public class ConsecutiveLectureException extends BITSConstraintException {
    public ConsecutiveLectureException(String courseCode, int day1, int day2) {
        super("Course " + courseCode + " has lectures on consecutive days: " + 
              getDayName(day1) + " and " + getDayName(day2));
    }
    
    private static String getDayName(int day) {
        String[] days = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday"};
        return day >= 0 && day < days.length ? days[day] : "Unknown";
    }
}
