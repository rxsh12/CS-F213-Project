package timetable_plus.exceptions;

public class CreditLimitException extends BITSConstraintException {
    public CreditLimitException(String studentId, int currentCredits, int attemptedCredits) {
        super("Student " + studentId + " cannot exceed 25 credits. " +
              "Current: " + currentCredits + ", Attempted to add: " + attemptedCredits + 
              ", Total would be: " + (currentCredits + attemptedCredits));
    }
}