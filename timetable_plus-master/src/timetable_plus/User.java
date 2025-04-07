package timetable_plus;

import java.io.Serializable;

public abstract class User implements Serializable {
    protected String username;
    protected String password;
    protected String role;
    
    public User(String username, String password, String role) {
        this.username = username;
        this.password = password;
        this.role = role;
    }
    
    public String getUsername() { return username; }
    public String getRole() { return role; }
    
    public boolean checkPassword(String inputPassword) {
        return password.equals(inputPassword);
    }
}

// Admin Class
class Admin extends User {
    public Admin(String username, String password) {
        super(username, password, "Admin");
    }
}

// Faculty Class
class Faculty extends User {
    private String facultyId;
    private String name;
    
    public Faculty(String username, String password, String facultyId, String name) {
        super(username, password, "Faculty");
        this.facultyId = facultyId;
        this.name = name;
    }
    
    public String getFacultyId() { return facultyId; }
    public String getName() { return name; }
}

// Student Class
class Student extends User {
    private String studentId;
    private int currentCredits;
    
    public Student(String username, String password, String studentId) {
        super(username, password, "Student");
        this.studentId = studentId;
        this.currentCredits = 0;
    }
    
    public String getStudentId() { return studentId; }
    public int getCurrentCredits() { return currentCredits; }
    public void addCredits(int credits) { this.currentCredits += credits; }
}
