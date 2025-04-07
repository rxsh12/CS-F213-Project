package timetable_plus;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryStore implements Serializable {
    // BITS academic constants
    public static final int WORKING_DAYS = 5;
    public static final int PERIODS_PER_DAY = 9;
    
    // Entity storage
    private Map<String, Course> courses;
    private Map<String, Instructor> instructors;
    private Map<String, Classroom> classrooms;
    private List<Schedule> schedules;
    private Map<String, User> users;
    
    // Singleton pattern
    private static InMemoryStore instance;
    
    private InMemoryStore() {
        courses = new HashMap<>();
        instructors = new HashMap<>();
        classrooms = new HashMap<>();
        schedules = new ArrayList<>();
        users = new HashMap<>();
    }
    
    public static InMemoryStore getInstance() {
        if(instance == null) {
            instance = new InMemoryStore();
        }
        return instance;
    }
    
    // Data management methods
    public void addCourse(Course course) {
        courses.put(course.getCourseCode(), course);
    }
    
    public void addInstructor(Instructor instructor) {
        instructors.put(instructor.getInstructorId(), instructor);
    }
    
    public void addClassroom(Classroom classroom) {
        classrooms.put(classroom.getRoomNumber(), classroom);
    }
    
    public void addSchedule(Schedule schedule) {
        schedules.add(schedule);
    }
    
    public void addUser(User user) {
        users.put(user.getUsername(), user);
    }
    
    // Getters
    public Course getCourse(String courseCode) {
        return courses.get(courseCode);
    }
    
    public Instructor getInstructor(String instructorId) {
        return instructors.get(instructorId);
    }
    
    public Classroom getClassroom(String roomNumber) {
        return classrooms.get(roomNumber);
    }
    
    public User getUser(String username) {
        return users.get(username);
    }
    
    public Map<String, Course> getAllCourses() {
        return courses;
    }
    
    public Map<String, Instructor> getAllInstructors() {
        return instructors;
    }
    
    public Map<String, Classroom> getAllClassrooms() {
        return classrooms;
    }
    
    public List<Schedule> getAllSchedules() {
        return schedules;
    }
    
    // Faculty workload tracking
    public int getFacultyLectureCount(String facultyId) {
        Instructor instructor = instructors.get(facultyId);
        return instructor != null ? instructor.getLecturesAssigned() : 0;
    }
    
    public int getFacultyTutorialCount(String facultyId) {
        Instructor instructor = instructors.get(facultyId);
        return instructor != null ? instructor.getTutorialsAssigned() : 0;
    }
    
    public int getFacultyLabCount(String facultyId) {
        Instructor instructor = instructors.get(facultyId);
        return instructor != null ? instructor.getLabsAssigned() : 0;
    }
    
    // Authentication
    public User authenticate(String username, String password, String role) {
        User user = users.get(username);
        if(user != null && user.checkPassword(password) && user.getRole().equals(role)) {
            return user;
        }
        return null;
    }
    
    // Student enrollment validation
    public boolean validateEnrollment(String studentId, List<Course> courses) {
        User user = null;
        for(User u : users.values()) {
            if(u instanceof Student && ((Student)u).getStudentId().equals(studentId)) {
                user = u;
                break;
            }
        }
        
        if(user == null) return false;
        
        Student student = (Student)user;
        int totalCredits = student.getCurrentCredits();
        
        for(Course course : courses) {
            totalCredits += course.getCredits();
        }
        
        return totalCredits <= BITSConstraints.MAX_CREDITS;
    }
    
    // Schedule retrieval
    public Schedule getCurrentSchedule() {
        if(schedules.isEmpty()) {
            schedules.add(new Schedule("Default"));
        }
        return schedules.get(0);
    }
    
    public Schedule getStudentSchedule(String studentId) {
        // Implementation to get student-specific schedule
        return getCurrentSchedule();
    }
    
    public Schedule getFacultySchedule(String facultyId) {
        // Implementation to get faculty-specific schedule
        return getCurrentSchedule();
    }
    
    // Serialization
    public void saveToFile(String filename) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filename))) {
            oos.writeObject(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public static void loadFromFile(String filename) {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filename))) {
            instance = (InMemoryStore) ois.readObject();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    // Initial data loading
    public void loadInitialData() {
        // Sample data for testing
        initializeRooms();
        initializeInstructors();
        initializeCourses();
        initializeUsers();
    }
    
    private void initializeRooms() {
        // Lecture halls
        addClassroom(new Classroom("F101", 350).withAV(true));
        addClassroom(new Classroom("F102", 350).withAV(true));
        addClassroom(new Classroom("F103", 200).withAV(true));
        addClassroom(new Classroom("F104", 200).withAV(true));
        addClassroom(new Classroom("F105", 350).withAV(true));
        addClassroom(new Classroom("F106", 200).withAV(true));
        
        // Tutorial rooms
        for(int i = 1; i <= 20; i++) {
            addClassroom(new Classroom("I" + (100 + i), 80).withAV(true));
        }
        
        // Labs
        addClassroom(new Classroom("D311", 80).withAV(true).withComputers(80));
        addClassroom(new Classroom("D312", 80).withAV(true).withComputers(80));
        addClassroom(new Classroom("D313", 80).withAV(true).withComputers(80));
        addClassroom(new Classroom("I012", 40).withWorkbenches(true));
        addClassroom(new Classroom("I013", 40).withWorkbenches(true));
    }
    
    private void initializeInstructors() {
        addInstructor(new Instructor("I001", "Dr. Abhijit Das"));
        addInstructor(new Instructor("I002", "Prof. Jabez Christopher"));
        addInstructor(new Instructor("I003", "Dr. Chittaranjan Hota"));
        addInstructor(new Instructor("I004", "Prof. Barsha Mitra"));
        addInstructor(new Instructor("I005", "Dr. Gurunarayan S"));
    }
    
    private void initializeCourses() {
        Course c1 = new Course("CS F213", "Object Oriented Programming");
        c1.setLectureHours(3);
        c1.setLabHours(2);
        c1.setCredits(4);
        c1.setEnrolledStudents(120);
        c1.addInstructor("I001");
        c1.setDisplayColor(new java.awt.Color(173, 216, 230)); // Light blue
        
        Course c2 = new Course("CS F222", "Discrete Structures for CS");
        c2.setLectureHours(3);
        c2.setCredits(3);
        c2.setEnrolledStudents(150);
        c2.addInstructor("I002");
        c2.setDisplayColor(new java.awt.Color(144, 238, 144)); // Light green
        
        Course c3 = new Course("CS F214", "Logic in Computer Science");
        c3.setLectureHours(3);
        c3.setCredits(3);
        c3.setEnrolledStudents(100);
        c3.addInstructor("I003");
        c3.setDisplayColor(new java.awt.Color(255, 182, 193)); // Light pink
        
        addCourse(c1);
        addCourse(c2);
        addCourse(c3);
    }
    
    private void initializeUsers() {
        addUser(new Admin("admin", "admin123"));
        addUser(new Faculty("faculty1", "faculty123", "I001", "Dr. Sharma"));
        addUser(new Faculty("faculty2", "faculty123", "I002", "Prof. Gupta"));
        addUser(new Student("student1", "student123", "2022A7PS0001H"));
        addUser(new Student("student2", "student123", "2022A7PS0002H"));
    }
}

//  retrieve all users
public List<User> getAllUsers() {
    return new ArrayList<>(users.values());
}

//  retrieve all schedules
public List<Schedule> getAllSchedules() {
    return schedules;
}
