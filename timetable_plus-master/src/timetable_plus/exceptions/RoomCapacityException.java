package timetable_plus.exceptions;

public class RoomCapacityException extends BITSConstraintException {
    public RoomCapacityException(String roomId, int capacity, int requiredCapacity) {
        super("Room " + roomId + " has insufficient capacity: " + capacity + 
              ", but required: " + requiredCapacity);
    }
}