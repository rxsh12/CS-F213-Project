package timetable_plus;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.dnd.*;
import java.io.IOException;

public class DragDropManager implements DragGestureListener, DropTargetListener {
    private Component component;
    private Schedule schedule;
    private Course selectedCourse;
    
    public DragDropManager(Component component, Schedule schedule) {
        this.component = component;
        this.schedule = schedule;
        
        // Set up drag source
        DragSource dragSource = DragSource.getDefaultDragSource();
        dragSource.createDefaultDragGestureRecognizer(
            component, DnDConstants.ACTION_MOVE, this);
        
        // Set up drop target
        new DropTarget(component, this);
    }
    
    @Override
    public void dragGestureRecognized(DragGestureEvent dge) {
        // Get the dragged course
        Point clickPoint = dge.getDragOrigin();
        selectedCourse = getCourseAtPoint(clickPoint);
        
        if (selectedCourse != null) {
            Transferable transferable = new CourseTransferable(selectedCourse);
            dge.startDrag(null, transferable);
        }
    }
    
    @Override
    public void dragEnter(DropTargetDragEvent dtde) {
        dtde.acceptDrag(DnDConstants.ACTION_MOVE);
    }
    
    @Override
    public void dragOver(DropTargetDragEvent dtde) {
        // Highlight valid drop targets
    }
    
    @Override
    public void dropActionChanged(DropTargetDragEvent dtde) {
    }
    
    @Override
    public void dragExit(DropTargetEvent dte) {
    }
    
    @Override
    public void drop(DropTargetDropEvent dtde) {
        try {
            Transferable transferable = dtde.getTransferable();
            Course course = (Course) transferable.getTransferData(CourseTransferable.COURSE_FLAVOR);
            
            Point dropPoint = dtde.getLocation();
            ScheduleSlot targetSlot = getSlotAtPoint(dropPoint);
            
            if (targetSlot != null && !targetSlot.isLocked()) {
                // Check constraints before allowing drop
                if (validateDrop(course, targetSlot)) {
                    targetSlot.setCourse(course);
                    dtde.dropComplete(true);
                    // Refresh view
                    component.repaint();
                } else {
                    dtde.rejectDrop();
                }
            } else {
                dtde.rejectDrop();
            }
        } catch (Exception e) {
            dtde.rejectDrop();
        }
    }
    
    private Course getCourseAtPoint(Point point) {
        // Convert point to day/period
        int day = point.y / 50; // Adjust based on cell size
        int period = point.x / 50;
        
        if (day >= 0 && day < InMemoryStore.WORKING_DAYS &&
            period >= 0 && period < InMemoryStore.PERIODS_PER_DAY) {
            return schedule.getSlot(day, period).getCourse();
        }
        
        return null;
    }
    
    private ScheduleSlot getSlotAtPoint(Point point) {
        // Convert point to day/period
        int day = point.y / 50; // Adjust based on cell size
        int period = point.x / 50;
        
        if (day >= 0 && day < InMemoryStore.WORKING_DAYS &&
            period >= 0 && period < InMemoryStore.PERIODS_PER_DAY) {
            return schedule.getSlot(day, period);
        }
        
        return null;
    }
    
    private boolean validateDrop(Course course, ScheduleSlot targetSlot) {
        try {
            // Check constraints
            BITSConstraints.validateLunchBreak(targetSlot.getPeriod());
            
            // Check for conflicts
            if (BITSConflictChecker.hasRoomConflict(targetSlot.getRoomNumber(), 
                                                  targetSlot.getDay(), 
                                                  targetSlot.getPeriod(), 
                                                  schedule)) {
                return false;
            }
            
            if (BITSConflictChecker.hasInstructorConflict(targetSlot.getInstructorId(), 
                                                        targetSlot.getDay(), 
                                                        targetSlot.getPeriod(), 
                                                        schedule)) {
                return false;
            }
            
            // Check lecture spacing
            if (BITSConflictChecker.hasCourseConflict(course, targetSlot.getDay(), schedule)) {
                return false;
            }
            
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    // Custom Transferable for courses
    private static class CourseTransferable implements Transferable {
        public static final DataFlavor COURSE_FLAVOR = 
            new DataFlavor(Course.class, "Course");
        
        private Course course;
        
        public CourseTransferable(Course course) {
            this.course = course;
        }
        
        @Override
        public DataFlavor[] getTransferDataFlavors() {
            return new DataFlavor[]{COURSE_FLAVOR};
        }
        
        @Override
        public boolean isDataFlavorSupported(DataFlavor flavor) {
            return flavor.equals(COURSE_FLAVOR);
        }
        
        @Override
        public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
            if (flavor.equals(COURSE_FLAVOR)) {
                return course;
            } else {
                throw new UnsupportedFlavorException(flavor);
            }
        }
    }
}
