package timetable_plus.exceptions;

public class LabDurationException extends BITSConstraintException {
    public LabDurationException(int duration) {
        super("Lab sessions must be exactly 2 hours long. Current duration: " + duration + " hours");
    }
}