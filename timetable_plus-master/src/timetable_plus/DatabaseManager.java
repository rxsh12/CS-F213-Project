package timetable_plus;

import java.sql.*;

public class DatabaseManager {
    private static final String DB_URL = "jdbc:sqlite:timetable.db";
    
    public static void initialize() throws SQLException {
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement()) {
            
            // Create tables if they don't exist
            createTables(stmt);
            
            // Create schema version tracking
            createVersionTable(stmt);
        }
    }
    
    private static void createTables(Statement stmt) throws SQLException {
        // Classrooms table
        stmt.execute(
            "CREATE TABLE IF NOT EXISTS classrooms (" +
            "room_number TEXT PRIMARY KEY, " +
            "capacity INTEGER, " +
            "is_lab BOOLEAN, " +
            "has_av BOOLEAN)"
        );
        
        // Courses table
        stmt.execute(
            "CREATE TABLE IF NOT EXISTS courses (" +
            "course_code TEXT PRIMARY KEY, " +
            "course_name TEXT, " +
            "credits INTEGER, " +
            "lecture_hours INTEGER, " +
            "lab_hours INTEGER, " +
            "enrolled_students INTEGER)"
        );
        
        // Instructors table
        stmt.execute(
            "CREATE TABLE IF NOT EXISTS instructors (" +
            "instructor_id TEXT PRIMARY KEY, " +
            "name TEXT, " +
            "lectures_assigned INTEGER, " +
            "tutorials_assigned INTEGER, " +
            "labs_assigned INTEGER)"
        );
        
        // Course-Instructor mapping table
        stmt.execute(
            "CREATE TABLE IF NOT EXISTS course_instructors (" +
            "course_code TEXT, " +
            "instructor_id TEXT, " +
            "PRIMARY KEY (course_code, instructor_id), " +
            "FOREIGN KEY (course_code) REFERENCES courses(course_code), " +
            "FOREIGN KEY (instructor_id) REFERENCES instructors(instructor_id))"
        );
        
        // Schedule table
        stmt.execute(
            "CREATE TABLE IF NOT EXISTS schedules (" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "name TEXT, " +
            "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP)"
        );
        
        // Schedule slots table
        stmt.execute(
            "CREATE TABLE IF NOT EXISTS schedule_slots (" +
            "schedule_id INTEGER, " +
            "day INTEGER, " +
            "period INTEGER, " +
            "course_code TEXT, " +
            "room_number TEXT, " +
            "instructor_id TEXT, " +
            "PRIMARY KEY (schedule_id, day, period), " +
            "FOREIGN KEY (schedule_id) REFERENCES schedules(id), " +
            "FOREIGN KEY (course_code) REFERENCES courses(course_code), " +
            "FOREIGN KEY (room_number) REFERENCES classrooms(room_number), " +
            "FOREIGN KEY (instructor_id) REFERENCES instructors(instructor_id))"
        );
        
        // Users table
        stmt.execute(
            "CREATE TABLE IF NOT EXISTS users (" +
            "username TEXT PRIMARY KEY, " +
            "password TEXT, " +
            "role TEXT, " +
            "additional_id TEXT, " +
            "additional_info TEXT)"
        );
    }
    
    private static void createVersionTable(Statement stmt) throws SQLException {
        stmt.execute(
            "CREATE TABLE IF NOT EXISTS schema_version (version INTEGER)"
        );
        
        // Check if version exists
        ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM schema_version");
        if (rs.next() && rs.getInt(1) == 0) {
            // Insert initial version
            stmt.execute("INSERT INTO schema_version VALUES (1)");
        }
    }
    
    public static void saveClassroom(Classroom room) throws SQLException {
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement ps = conn.prepareStatement(
                 "INSERT OR REPLACE INTO classrooms VALUES (?, ?, ?, ?)")) {
            
            ps.setString(1, room.getRoomNumber());
            ps.setInt(2, room.getCapacity());
            ps.setBoolean(3, room.isLab());
            ps.setBoolean(4, room.hasAVEquipment());
            
            ps.executeUpdate();
        }
    }
    
    public static void saveCourse(Course course) throws SQLException {
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement ps = conn.prepareStatement(
                 "INSERT OR REPLACE INTO courses VALUES (?, ?, ?, ?, ?, ?)")) {
            
            ps.setString(1, course.getCourseCode());
            ps.setString(2, course.getCourseName());
            ps.setInt(3, course.getCredits());
            ps.setInt(4, course.getLectureHours());
            ps.setInt(5, course.getLabHours());
            ps.setInt(6, course.getEnrolledStudents());
            
            ps.executeUpdate();
        }
    }
    
    public static void saveSchedule(Schedule schedule) throws SQLException {
        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            conn.setAutoCommit(false);
            
            // Insert schedule
            try (PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO schedules (name) VALUES (?)", Statement.RETURN_GENERATED_KEYS)) {
                
                ps.setString(1, schedule.getName());
                ps.executeUpdate();
                
                // Get generated ID
                ResultSet rs = ps.getGeneratedKeys();
                int scheduleId = rs.next() ? rs.getInt(1) : -1;
                
                if (scheduleId > 0) {
                    // Insert schedule slots
                    try (PreparedStatement slotPs = conn.prepareStatement(
                        "INSERT INTO schedule_slots VALUES (?, ?, ?, ?, ?, ?)")) {
                        
                        for (int day = 0; day < InMemoryStore.WORKING_DAYS; day++) {
                            for (int period = 0; period < InMemoryStore.PERIODS_PER_DAY; period++) {
                                ScheduleSlot slot = schedule.getSlot(day, period);
                                
                                if (slot != null && slot.getCourse() != null) {
                                    slotPs.setInt(1, scheduleId);
                                    slotPs.setInt(2, day);
                                    slotPs.setInt(3, period);
                                    slotPs.setString(4, slot.getCourse().getCourseCode());
                                    slotPs.setString(5, slot.getRoomNumber());
                                    slotPs.setString(6, slot.getInstructorId());
                                    
                                    slotPs.addBatch();
                                }
                            }
                        }
                        
                        slotPs.executeBatch();
                    }
                }
            }
            
            conn.commit();
        }
    }
    
    public static void loadClassrooms() throws SQLException {
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM classrooms")) {
            
            while (rs.next()) {
                String roomNumber = rs.getString("room_number");
                int capacity = rs.getInt("capacity");
                boolean isLab = rs.getBoolean("is_lab");
                boolean hasAV = rs.getBoolean("has_av");
                
                Classroom room = new Classroom(roomNumber, capacity)
                    .withAV(hasAV);
                
                InMemoryStore.getInstance().addClassroom(room);
            }
        }
    }
    
    public static void loadCourses() throws SQLException {
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM courses")) {
            
            while (rs.next()) {
                String courseCode = rs.getString("course_code");
                String courseName = rs.getString("course_name");
                int credits = rs.getInt("credits");
                int lectureHours = rs.getInt("lecture_hours");
                int labHours = rs.getInt("lab_hours");
                int enrolledStudents = rs.getInt("enrolled_students");
                
                Course course = new Course(courseCode, courseName);
                course.setCredits(credits);
                course.setLectureHours(lectureHours);
                course.setLabHours(labHours);
                course.setEnrolledStudents(enrolledStudents);
                
                InMemoryStore.getInstance().addCourse(course);
            }
        }
    }
    
    public static void loadSchedules() throws SQLException {
        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            // First load all schedules
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT * FROM schedules")) {
                
                while (rs.next()) {
                    int scheduleId = rs.getInt("id");
                    String name = rs.getString("name");
                    
                    Schedule schedule = new Schedule(name);
                    
                    // Load slots for this schedule
                    try (PreparedStatement ps = conn.prepareStatement(
                        "SELECT * FROM schedule_slots WHERE schedule_id = ?")) {
                        
                        ps.setInt(1, scheduleId);
                        ResultSet slotRs = ps.executeQuery();
                        
                        while (slotRs.next()) {
                            int day = slotRs.getInt("day");
                            int period = slotRs.getInt("period");
                            String courseCode = slotRs.getString("course_code");
                            String roomNumber = slotRs.getString("room_number");
                            String instructorId = slotRs.getString("instructor_id");
                            
                            Course course = InMemoryStore.getInstance().getCourse(courseCode);
                            if (course != null) {
                                ScheduleSlot slot = new ScheduleSlot(course, roomNumber, instructorId);
                                schedule.setSlot(day, period, slot);
                            }
                        }
                    }
                    
                    InMemoryStore.getInstance().addSchedule(schedule);
                }
            }
        }
    }
    
    public static void saveInstructor(Instructor instructor) throws SQLException {
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement ps = conn.prepareStatement(
                 "INSERT OR REPLACE INTO instructors VALUES (?, ?, ?, ?, ?)")) {
            
            ps.setString(1, instructor.getInstructorId());
            ps.setString(2, instructor.getName());
            ps.setInt(3, instructor.getLecturesAssigned());
            ps.setInt(4, instructor.getTutorialsAssigned());
            ps.setInt(5, instructor.getLabsAssigned());
            
            ps.executeUpdate();
        }
    }
    
    public static void loadInstructors() throws SQLException {
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM instructors")) {
            
            while (rs.next()) {
                String id = rs.getString("instructor_id");
                String name = rs.getString("name");
                int lectures = rs.getInt("lectures_assigned");
                int tutorials = rs.getInt("tutorials_assigned");
                int labs = rs.getInt("labs_assigned");
                
                Instructor instructor = new Instructor(id, name)
                    .withAssignedLectures(lectures)
                    .withAssignedTutorials(tutorials)
                    .withAssignedLabs(labs);
                
                InMemoryStore.getInstance().addInstructor(instructor);
            }
        }
    }
}
