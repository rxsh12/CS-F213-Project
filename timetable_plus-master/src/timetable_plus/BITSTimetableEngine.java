package timetable_plus;

import java.util.*;
import timetable_plus.exceptions.*;

public class BITSTimetableEngine {
    // Genetic algorithm parameters
    private static final int POPULATION_SIZE = 100;
    private static final double MUTATION_RATE = 0.02;
    private static final int TOURNAMENT_SIZE = 5;
    private static final int MAX_GENERATIONS = 1000;
    
    private InMemoryStore store;
    private Random random = new Random();
    
    public BITSTimetableEngine() {
        this.store = InMemoryStore.getInstance();
    }
    
    public Schedule generateSchedule() {
        Schedule schedule = new Schedule("Generated " + new Date().toString());
        
        try {
            // First allocate labs (they need consecutive slots)
            allocateLabSessions(schedule);
            
            // Then allocate lectures with day gaps
            allocateLectureSessions(schedule);
            
            // Finally allocate tutorials
            allocateTutorialSessions(schedule);
            
            // Optimize using genetic algorithm
            optimizeSchedule(schedule);
            
            return schedule;
        } catch (Exception e) {
            e.printStackTrace();
            return schedule;
        }
    }
    
    private void allocateLabSessions(Schedule schedule) throws BITSConstraintException {
        for (Course course : store.getAllCourses().values()) {
            if (course.getLabHours() > 0) {
                allocateLabForCourse(course, schedule);
            }
        }
    }
    
    private void allocateLabForCourse(Course course, Schedule schedule) throws BITSConstraintException {
        List<Classroom> suitableLabs = findSuitableLabs(course);
        if (suitableLabs.isEmpty()) return;
        
        List<String> eligibleInstructors = findEligibleInstructors(course, "lab");
        if (eligibleInstructors.isEmpty()) return;
        
        boolean allocated = false;
        int attempts = 0;
        int maxAttempts = 50;
        
        while (!allocated && attempts < maxAttempts) {
            attempts++;
            
            // Random day and period
            int day = random.nextInt(InMemoryStore.WORKING_DAYS);
            int period = random.nextInt(InMemoryStore.PERIODS_PER_DAY - BITSConstraints.LAB_DURATION + 1);
            
            // Skip lunch periods
            if (period >= BITSConstraints.LUNCH_START - BITSConstraints.LAB_DURATION + 1 && 
                period < BITSConstraints.LUNCH_END) {
                continue;
            }
            
            // Try each instructor and lab until successful
            for (String instructorId : eligibleInstructors) {
                for (Classroom lab : suitableLabs) {
                    if (BITSConflictChecker.isValidAllocation(course, lab.getRoomNumber(), 
                                                            instructorId, day, period, schedule)) {
                        // Allocate lab session (2 consecutive slots)
                        for (int i = 0; i < BITSConstraints.LAB_DURATION; i++) {
                            schedule.getSlot(day, period + i).allocate(
                                course, lab.getRoomNumber(), instructorId);
                        }
                        
                        // Update instructor workload
                        store.getInstructor(instructorId).assignCourse(course.getCourseCode(), "lab");
                        allocated = true;
                        break;
                    }
                }
                if (allocated) break;
            }
        }
    }
    
    private void allocateLectureSessions(Schedule schedule) throws BITSConstraintException {
        for (Course course : store.getAllCourses().values()) {
            if (course.getLectureHours() > 0) {
                allocateLecturesForCourse(course, schedule);
            }
        }
    }
    
    private void allocateLecturesForCourse(Course course, Schedule schedule) throws BITSConstraintException {
        List<Classroom> suitableLectureRooms = findSuitableLectureRooms(course);
        if (suitableLectureRooms.isEmpty()) return;
        
        List<String> eligibleInstructors = findEligibleInstructors(course, "lecture");
        if (eligibleInstructors.isEmpty()) return;
        
        int lecturesAllocated = 0;
        int maxAttempts = 100;
        
        // Typically 3 lectures per week
        while (lecturesAllocated < course.getLectureHours() && lecturesAllocated < maxAttempts) {
            int day = random.nextInt(InMemoryStore.WORKING_DAYS);
            int period = random.nextInt(InMemoryStore.PERIODS_PER_DAY);
            
            // Skip lunch periods
            if (period >= BITSConstraints.LUNCH_START && period < BITSConstraints.LUNCH_END) {
                continue;
            }
            
            // Skip days that would violate the day gap constraint
            if (BITSConflictChecker.hasCourseConflict(course, day, schedule)) {
                continue;
            }
            
            // Try each instructor and room
            for (String instructorId : eligibleInstructors) {
                for (Classroom room : suitableLectureRooms) {
                    if (BITSConflictChecker.isValidAllocation(course, room.getRoomNumber(), 
                                                            instructorId, day, period, schedule)) {
                        schedule.getSlot(day, period).allocate(
                            course, room.getRoomNumber(), instructorId);
                        
                        store.getInstructor(instructorId).assignCourse(course.getCourseCode(), "lecture");
                        lecturesAllocated++;
                        break;
                    }
                }
                if (lecturesAllocated > 0) break;
            }
        }
    }
    
    private void allocateTutorialSessions(Schedule schedule) throws BITSConstraintException {
        for (Course course : store.getAllCourses().values()) {
            if (course.getTutorialHours() > 0) {
                allocateTutorialForCourse(course, schedule);
            }
        }
    }
    
    private void allocateTutorialForCourse(Course course, Schedule schedule) throws BITSConstraintException {
        // Similar implementation to lectures but for tutorials
    }
    
    private List<Classroom> findSuitableLabs(Course course) {
        List<Classroom> suitableLabs = new ArrayList<>();
        
        for (Classroom room : store.getAllClassrooms().values()) {
            if (room.isLab() && room.canHostCourse(course)) {
                suitableLabs.add(room);
            }
        }
        
        return suitableLabs;
    }
    
    private List<Classroom> findSuitableLectureRooms(Course course) {
        List<Classroom> suitableRooms = new ArrayList<>();
        
        for (Classroom room : store.getAllClassrooms().values()) {
            if (!room.isLab() && room.canHostCourse(course)) {
                suitableRooms.add(room);
            }
        }
        
        return suitableRooms;
    }
    
    private List<String> findEligibleInstructors(Course course, String sessionType) {
        List<String> eligible = new ArrayList<>();
        
        for (String instructorId : course.getInstructors()) {
            Instructor instructor = store.getInstructor(instructorId);
            if (instructor != null && instructor.canTeach(sessionType)) {
                eligible.add(instructorId);
            }
        }
        
        return eligible;
    }
    
    private void optimizeSchedule(Schedule schedule) {
        // Implement genetic algorithm optimization
        List<Schedule> population = initializePopulation(schedule);
        
        for (int generation = 0; generation < MAX_GENERATIONS; generation++) {
            population = evolvePopulation(population);
            
            // Early termination if we have a perfect solution
            Schedule best = getBestSchedule(population);
            if (calculateFitness(best) >= 0.95) {
                copySchedule(best, schedule);
                return;
            }
        }
        
        // Use the best schedule found
        Schedule best = getBestSchedule(population);
        copySchedule(best, schedule);
    }
    
    private List<Schedule> initializePopulation(Schedule base) {
        List<Schedule> population = new ArrayList<>();
        population.add(base); // Keep the original schedule
        
        for (int i = 1; i < POPULATION_SIZE; i++) {
            Schedule mutated = mutate(cloneSchedule(base));
            population.add(mutated);
        }
        
        return population;
    }
    
    private List<Schedule> evolvePopulation(List<Schedule> population) {
        List<Schedule> newPopulation = new ArrayList<>();
        
        // Elitism - keep the best schedules
        int eliteCount = POPULATION_SIZE / 10;
        List<Schedule> sortedPopulation = new ArrayList<>(population);
        sortedPopulation.sort((s1, s2) -> Double.compare(calculateFitness(s2), calculateFitness(s1)));
        
        for (int i = 0; i < eliteCount; i++) {
            newPopulation.add(cloneSchedule(sortedPopulation.get(i)));
        }
        
        // Fill rest with crossover and mutation
        while (newPopulation.size() < POPULATION_SIZE) {
            Schedule parent1 = tournamentSelection(population);
            Schedule parent2 = tournamentSelection(population);
            
            Schedule child = crossover(parent1, parent2);
            
            if (random.nextDouble() < MUTATION_RATE) {
                child = mutate(child);
            }
            
            newPopulation.add(child);
        }
        
        return newPopulation;
    }
    
    private Schedule tournamentSelection(List<Schedule> population) {
        List<Schedule> tournament = new ArrayList<>();
        for (int i = 0; i < TOURNAMENT_SIZE; i++) {
            tournament.add(population.get(random.nextInt(population.size())));
        }
        
        return getBestSchedule(tournament);
    }
    
    private Schedule crossover(Schedule parent1, Schedule parent2) {
        Schedule child = new Schedule("Crossover");
        
        // Random crossover point
        int day = random.nextInt(InMemoryStore.WORKING_DAYS);
        int period = random.nextInt(InMemoryStore.PERIODS_PER_DAY);
        
        // Copy from parent1 before crossover point
        for (int d = 0; d < InMemoryStore.WORKING_DAYS; d++) {
            for (int p = 0; p < InMemoryStore.PERIODS_PER_DAY; p++) {
                if (d < day || (d == day && p <= period)) {
                    copySlot(parent1.getSlot(d, p), child.getSlot(d, p));
                } else {
                    copySlot(parent2.getSlot(d, p), child.getSlot(d, p));
                }
            }
        }
        
        return child;
    }
    
    private Schedule mutate(Schedule schedule) {
        // Randomly swap some slots
        int mutations = 1 + random.nextInt(3); // 1-3 mutations
        
        for (int i = 0; i < mutations; i++) {
            int day1 = random.nextInt(InMemoryStore.WORKING_DAYS);
            int period1 = random.nextInt(InMemoryStore.PERIODS_PER_DAY);
            int day2 = random.nextInt(InMemoryStore.WORKING_DAYS);
            int period2 = random.nextInt(InMemoryStore.PERIODS_PER_DAY);
            
            ScheduleSlot slot1 = schedule.getSlot(day1, period1);
            ScheduleSlot slot2 = schedule.getSlot(day2, period2);
            
            // Don't mutate locked slots or lunch time
            if (slot1.isLocked() || slot2.isLocked() ||
                !BITSConstraints.isValidLunchBreak(period1) || 
                !BITSConstraints.isValidLunchBreak(period2)) {
                continue;
            }
            
            // Swap slots
            Course tempCourse = slot1.getCourse();
            String tempRoom = slot1.getRoomNumber();
            String tempInstructor = slot1.getInstructorId();
            
            slot1.setCourse(slot2.getCourse());
            slot1.setRoomNumber(slot2.getRoomNumber());
            slot1.setInstructorId(slot2.getInstructorId());
            
            slot2.setCourse(tempCourse);
            slot2.setRoomNumber(tempRoom);
            slot2.setInstructorId(tempInstructor);
        }
        
        return schedule;
    }
    
    private double calculateFitness(Schedule schedule) {
        int conflicts = 0;
        
        // Check all constraints
        for (int day = 0; day < InMemoryStore.WORKING_DAYS; day++) {
            for (int period = 0; period < InMemoryStore.PERIODS_PER_DAY; period++) {
                ScheduleSlot slot = schedule.getSlot(day, period);
                
                if (slot.getCourse() == null) continue;
                
                // Check lunch break
                if (!BITSConstraints.isValidLunchBreak(period)) {
                    conflicts++;
                }
                
                // Check instructor conflicts
                if (BITSConflictChecker.hasInstructorConflict(slot.getInstructorId(), day, period, schedule)) {
                    conflicts++;
                }
                
                // Check room conflicts
                if (BITSConflictChecker.hasRoomConflict(slot.getRoomNumber(), day, period, schedule)) {
                    conflicts++;
                }
                
                // Check lecture day spacing
                if (BITSConflictChecker.hasCourseConflict(slot.getCourse(), day, schedule)) {
                    conflicts++;
                }
            }
        }
        
        // Calculate fitness (0-1 range, higher is better)
        int maxPossibleConflicts = InMemoryStore.WORKING_DAYS * InMemoryStore.PERIODS_PER_DAY;
        return 1.0 - (double)conflicts / maxPossibleConflicts;
    }
    
    private Schedule getBestSchedule(List<Schedule> population) {
        Schedule best = population.get(0);
        double bestFitness = calculateFitness(best);
        
        for (Schedule schedule : population) {
            double fitness = calculateFitness(schedule);
            if (fitness > bestFitness) {
                best = schedule;
                bestFitness = fitness;
            }
        }
        
        return best;
    }
    
    private Schedule cloneSchedule(Schedule original) {
        Schedule clone = new Schedule(original.getName() + " (Clone)");
        
        for (int day = 0; day < InMemoryStore.WORKING_DAYS; day++) {
            for (int period = 0; period < InMemoryStore.PERIODS_PER_DAY; period++) {
                copySlot(original.getSlot(day, period), clone.getSlot(day, period));
            }
        }
        
        return clone;
    }
    
    private void copySchedule(Schedule source, Schedule target) {
        for (int day = 0; day < InMemoryStore.WORKING_DAYS; day++) {
            for (int period = 0; period < InMemoryStore.PERIODS_PER_DAY; period++) {
                copySlot(source.getSlot(day, period), target.getSlot(day, period));
            }
        }
    }
    
    private void copySlot(ScheduleSlot source, ScheduleSlot target) {
        target.setCourse(source.getCourse());
        target.setRoomNumber(source.getRoomNumber());
        target.setInstructorId(source.getInstructorId());
        target.setLocked(source.isLocked());
    }
}
