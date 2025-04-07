Here’s a detailed README file for your **BITS Pilani Timetable Builder Project**:

---

# **BITS Pilani Timetable Builder**
A Java-based GUI application for automated and manual timetable generation, designed to meet the specific requirements of BITS Pilani's academic policies. The system manages classrooms, instructors, courses, and time slots while enforcing constraints like lunch breaks, lab durations, and faculty workload limits.

---

## **Features**
### **1. Role-Based Access**
- **Admin Portal**:
  - Manage classrooms, courses, and instructors.
  - Generate timetables automatically using a genetic algorithm.
  - Export/import data via CSV.
  - View and edit schedules manually.
- **Faculty Portal**:
  - View assigned schedules.
  - Monitor workload (lectures, labs, tutorials).
- **Student Portal**:
  - Enroll in courses while adhering to the 25-credit limit.
  - View personalized timetables.

### **2. BITS-Specific Constraints**
- Lunch break enforcement (12 PM to 2 PM).
- Mandatory 2-hour lab sessions.
- Alternate-day lectures for each course.
- Faculty workload limits:
  - Max 1 lecture, 2 tutorials, and 1 lab per week.
- Room capacity checks based on enrolled students.

### **3. Automated Scheduling**
- Uses a genetic algorithm to optimize timetables:
  - Resolves conflicts (faculty, room, time slot).
  - Adheres to all constraints.

### **4. Manual Scheduling**
- Drag-and-drop functionality for manual timetable adjustments.
- Real-time conflict detection during manual edits.

### **5. Data Management**
- Import/export classrooms, courses, and instructors via CSV.
- Save and load schedules using a database (`SQLite`).

---

## **Project Structure**

```
src/
├── timetable_plus/
│   ├── AdminDashboard.java
│   ├── BITSConflictChecker.java
│   ├── BITSConstraints.java
│   ├── BITSTimetableEngine.java
│   ├── Classroom.java
│   ├── Course.java
│   ├── CSVHandler.java
│   ├── DatabaseManager.java
│   ├── DragDropManager.java
│   ├── DrawTimeTable.java
│   ├── FacultyPortal.java
│   ├── InMemoryStore.java
│   ├── Instructor.java
│   ├── LoginPage.java
│   ├── PrintTimeTable.java
│   ├── ScheduleSlot.java
│   ├── StudentPortal.java
│   ├── TimetablePanel.java
│   └── exceptions/
│       ├── BITSConstraintException.java
│       ├── CreditLimitException.java
│       ├── FacultyWorkloadException.java
│       ├── LabDurationException.java
│       ├── LunchBreakException.java
│       ├── ConsecutiveLectureException.java
│       └── RoomCapacityException.java
```

---

## **Setup Instructions**

### **1. Prerequisites**
- Java Development Kit (JDK) 8 or higher.
- An IDE like IntelliJ IDEA or Eclipse (optional but recommended).
- SQLite database setup (no external installation required; uses JDBC).

---

### **2. How to Run**
1. Clone the repository or download the source code.
2. Open the project in your IDE.
3. Compile the project:
    ```bash
    javac src/timetable_plus/*.java src/timetable_plus/exceptions/*.java
    ```
4. Run the `Main` class:
    ```bash
    java timetable_plus.Main
    ```

---

## **Usage**

### **Admin Portal**
1. Log in as an admin using credentials (`username: admin`, `password: admin123`).
2. Manage classrooms:
    - Add/edit/delete classrooms with attributes like capacity and AV equipment.
3. Manage courses:
    - Add/edit/delete courses with lecture/lab/tutorial details.
4. Generate timetables automatically or manually edit them.

### **Faculty Portal**
1. Log in as a faculty member (`username: faculty1`, `password: faculty123`).
2. View assigned schedules and workload statistics.

### **Student Portal**
1. Log in as a student (`username: student1`, `password: student123`).
2. Enroll in courses while adhering to credit limits.
3. View personalized timetables.

---

## **Key Classes**

### **1. `BITSTimetableEngine`**
Core engine for timetable generation using a genetic algorithm.

### **2. `AdminDashboard`**
GUI for managing classrooms, courses, instructors, and schedules.

### **3. `BITSConstraints`**
Implements all BITS-specific constraints:
- Lunch breaks.
- Lab durations.
- Lecture spacing.

### **4. `InMemoryStore`**
Stores all data in memory during runtime and provides methods for CRUD operations.

### **5. `DatabaseManager`**
Handles database persistence using SQLite.

---

## **Database Schema**

### Tables:
1. `classrooms`: Stores classroom details (capacity, AV equipment).
2. `courses`: Stores course details (code, name, credits).
3. `instructors`: Stores instructor details (ID, name).
4. `schedules`: Stores generated schedules with time slots.

---

## **Sample Data**

| Classroom | Capacity | Is Lab | AV Equipment |
|-----------|----------|--------|--------------|
| F101      | 350      | No     | Yes          |
| D311      | 80       | Yes    | Yes          |

| Course Code | Name                       | Credits | Lectures | Labs |
|-------------|----------------------------|---------|----------|------|
| CS F213     | Object-Oriented Programming| 4       | 3        | 2    |

---

## **Screenshots**

1. Admin Dashboard  
![Admin Dashboard](https://cdn.mathpix.com/cropped/2025_04_07_d5cea3847ed0875a7019g-08.jpgdule  
![Faculty Schedule](https://cdn.mathpix.com/cropped/2025_04_07_d5cea3847ed0875a7019g-12.jpgetable  
![Student Timetable](https://cdn.mathpix.com/cropped/2025_04_07_d5cea3847ed0875a7019g-13.jpgTesting**

Run unit tests using JUnit:
```bash
javac -cp .:junit.jar src/timetable_plus/tests/*.java 
java -cp .:junit.jar org.junit.runner.JUnitCore timetable_plus.tests.BITSTimetableTest 
```

---

## **Future Enhancements**
1. Add support for semester-long scheduling with holidays.
2. Integrate with an online portal for real-time updates.
3. Add support for importing/exporting schedules in `.xlsx` format.

---

