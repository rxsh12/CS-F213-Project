package timetable_plus;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

public class Instructor implements Serializable {
    private String id;
    private String name;
    private int lecturesAssigned;
    private int tutorialsAssigned;
    private int labsAssigned;
    private Set<String> assignedCourses;

    public Instructor(String id, String name) {
        this.id = id;
        this.name = name;
        this.assignedCourses = new HashSet<>();
        this.lecturesAssigned = 0;
        this.tutorialsAssigned = 0;
        this.labsAssigned = 0;
    }

    // Add builder pattern methods
    public Instructor withAssignedLectures(int count) {
        this.lecturesAssigned = count;
        return this;
    }

    public Instructor withAssignedTutorials(int count) {
        this.tutorialsAssigned = count;
        return this;
    }

    public Instructor withAssignedLabs(int count) {
        this.labsAssigned = count;
        return this;
    }

    public void assignCourse(String courseCode, String sessionType) {
        assignedCourses.add(courseCode);

        switch(sessionType.toLowerCase()) {
            case "lecture":
                lecturesAssigned++;
                break;
            case "tutorial":
                tutorialsAssigned++;
                break;
            case "lab":
                labsAssigned++;
                break;
        }
    }

    public boolean canTeach(String sessionType) {
        switch(sessionType.toLowerCase()) {
            case "lecture":
                return lecturesAssigned < BITSConstraints.MAX_LECTURES_PER_FACULTY;
            case "tutorial":
                return tutorialsAssigned < BITSConstraints.MAX_TUTORIALS_PER_FACULTY;
            case "lab":
                return labsAssigned < BITSConstraints.MAX_LABS_PER_FACULTY;
            default:
                return false;
        }
    }

    // Getters
    public String getInstructorId() { return id; }
    public String getName() { return name; }
    public int getLecturesAssigned() { return lecturesAssigned; }
    public int getTutorialsAssigned() { return tutorialsAssigned; }
    public int getLabsAssigned() { return labsAssigned; }
    public Set<String> getAssignedCourses() { return assignedCourses; }

    // Regular setters if needed
    public void setLecturesAssigned(int count) { this.lecturesAssigned = count; }
    public void setTutorialsAssigned(int count) { this.tutorialsAssigned = count; }
    public void setLabsAssigned(int count) { this.labsAssigned = count; }

    @Override
    public String toString() {
        return name + " (" + id + ")";
    }
}
