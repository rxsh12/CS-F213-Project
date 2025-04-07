package timetable_plus;

import java.awt.Color;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.ArrayList;
import java.util.List;

public class Course implements Serializable {
    private String courseCode; // e.g. "CS F213"
    private String courseName;
    private int enrolledStudents;
    private int lectureHours;
    private int tutorialHours;
    private int labHours;
    private int credits;
    private LinkedList<String> instructors;
    private Color displayColor;
    private List<String> incompatibleCourses;
    private boolean hasComprehensive;
    
    public Course(String code, String name) {
        this.courseCode = code;
        this.courseName = name;
        this.instructors = new LinkedList<>();
        this.incompatibleCourses = new ArrayList<>();
        this.displayColor = Color.WHITE;
    }
    
    // BITS-specific methods
    public void addInstructor(String instructorId) {
        if (!instructors.contains(instructorId)) {
            instructors.add(instructorId);
        }
    }
    
    public boolean conflictsWith(Course other) {
        return this.incompatibleCourses.contains(other.getCourseCode()) ||
               other.getIncompatibleCourses().contains(this.courseCode);
    }
    
    public void addIncompatibleCourse(String courseCode) {
        if (!incompatibleCourses.contains(courseCode)) {
            incompatibleCourses.add(courseCode);
        }
    }
    
    // Getters and setters
    public String getCourseCode() { return courseCode; }
    public void setCourseCode(String code) { this.courseCode = code; }
    
    public String getCourseName() { return courseName; }
    public void setCourseName(String name) { this.courseName = name; }
    
    public int getEnrolledStudents() { return enrolledStudents; }
    public void setEnrolledStudents(int count) { this.enrolledStudents = count; }
    
    public int getLectureHours() { return lectureHours; }
    public void setLectureHours(int hours) { this.lectureHours = hours; }
    
    public int getTutorialHours() { return tutorialHours; }
    public void setTutorialHours(int hours) { this.tutorialHours = hours; }
    
    public int getLabHours() { return labHours; }
    public void setLabHours(int hours) { this.labHours = hours; }
    
    public int getCredits() { return credits; }
    public void setCredits(int credits) { this.credits = credits; }
    
    public LinkedList<String> getInstructors() { return instructors; }
    
    public Color getDisplayColor() { return displayColor; }
    public void setDisplayColor(Color color) { this.displayColor = color; }
    
    public List<String> getIncompatibleCourses() { return incompatibleCourses; }
    
    public boolean hasComprehensive() { return hasComprehensive; }
    public void setHasComprehensive(boolean hasComprehensive) { 
        this.hasComprehensive = hasComprehensive;
    }
    
    @Override
    public String toString() {
        return courseCode + " - " + courseName;
    }
}
