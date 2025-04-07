package timetable_plus.exceptions;

public class LunchBreakException extends BITSConstraintException {
    public LunchBreakException() {
        super("Cannot schedule classes during lunch hours (12-2 PM)");
    }
}