package timetable_plus;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class CSVHandler {
    private static final String DELIMITER = ",";
    
    public static void exportClassrooms(String filename) throws IOException {
        try (PrintWriter writer = new PrintWriter(new File(filename))) {
            // Write header
            writer.println("RoomNumber,Capacity,IsLab,HasAVEquipment");
            
            // Write data rows
            for (Classroom room : InMemoryStore.getInstance().getAllClassrooms().values()) {
                StringBuilder sb = new StringBuilder();
                sb.append(room.getRoomNumber()).append(DELIMITER);
                sb.append(room.getCapacity()).append(DELIMITER);
                sb.append(room.isLab()).append(DELIMITER);
                sb.append(room.hasAVEquipment());
                
                writer.println(sb.toString());
            }
        }
    }
    
    public static void importClassrooms(String filename) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            // Skip header
            String line = reader.readLine();
            
            // Read data rows
            while ((line = reader.readLine()) != null) {
                String[] values = line.split(DELIMITER);
                
                if (values.length >= 4) {
                    String roomNumber = values[0].trim();
                    int capacity = Integer.parseInt(values[1].trim());
                    boolean isLab = Boolean.parseBoolean(values[2].trim());
                    boolean hasAV = Boolean.parseBoolean(values[3].trim());
                    
                    Classroom room = new Classroom(roomNumber, capacity)
                        .withAV(hasAV);
                    
                    InMemoryStore.getInstance().addClassroom(room);
                }
            }
        }
    }
    
    public static void exportCourses(String filename) throws IOException {
        try (PrintWriter writer = new PrintWriter(new File(filename))) {
            // Write header
            writer.println("CourseCode,CourseName,Credits,LectureHours,LabHours,EnrolledStudents");
            
            // Write data rows
            for (Course course : InMemoryStore.getInstance().getAllCourses().values()) {
                StringBuilder sb = new StringBuilder();
                sb.append(course.getCourseCode()).append(DELIMITER);
                sb.append(course.getCourseName()).append(DELIMITER);
                sb.append(course.getCredits()).append(DELIMITER);
                sb.append(course.getLectureHours()).append(DELIMITER);
                sb.append(course.getLabHours()).append(DELIMITER);
                sb.append(course.getEnrolledStudents());
                
                writer.println(sb.toString());
            }
        }
    }
    
    public static void importCourses(String filename) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            // Skip header
            String line = reader.readLine();
            
            // Read data rows
            while ((line = reader.readLine()) != null) {
                String[] values = line.split(DELIMITER);
                
                if (values.length >= 6) {
                    String courseCode = values[0].trim();
                    String courseName = values[1].trim();
                    int credits = Integer.parseInt(values[2].trim());
                    int lectureHours = Integer.parseInt(values[3].trim());
                    int labHours = Integer.parseInt(values[4].trim());
                    int students = Integer.parseInt(values[5].trim());
                    
                    Course course = new Course(courseCode, courseName);
                    course.setCredits(credits);
                    course.setLectureHours(lectureHours);
                    course.setLabHours(labHours);
                    course.setEnrolledStudents(students);
                    
                    InMemoryStore.getInstance().addCourse(course);
                }
            }
        }
    }
    
    public static void exportInstructors(String filename) throws IOException {
        try (PrintWriter writer = new PrintWriter(new File(filename))) {
            // Write header
            writer.println("InstructorID,Name");
            
            // Write data rows
            for (Instructor instructor : InMemoryStore.getInstance().getAllInstructors().values()) {
                StringBuilder sb = new StringBuilder();
                sb.append(instructor.getInstructorId()).append(DELIMITER);
                sb.append(instructor.getName());
                
                writer.println(sb.toString());
            }
        }
    }
    
    public static void importInstructors(String filename) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            // Skip header
            String line = reader.readLine();
            
            // Read data rows
            while ((line = reader.readLine()) != null) {
                String[] values = line.split(DELIMITER);
                
                if (values.length >= 2) {
                    String instructorID = values[0].trim();
                    String name = values[1].trim();
                    
                    Instructor instructor = new Instructor(instructorID, name);
                    InMemoryStore.getInstance().addInstructor(instructor);
                }
            }
        }
    }
}
