package timetable_plus.exceptions;

public class FacultyWorkloadException extends BITSConstraintException {
    public FacultyWorkloadException(String facultyId, String assignmentType) {
        super("Faculty " + facultyId + " has exceeded maximum allowed " + 
              assignmentType + " assignments");
    }
}