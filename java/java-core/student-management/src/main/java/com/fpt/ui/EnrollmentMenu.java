package com.fpt.ui;

import com.fpt.entity.ClassSection;
import com.fpt.entity.Course;
import com.fpt.entity.Schedule;
import com.fpt.entity.Semester;
import com.fpt.entity.Student;
import com.fpt.repository.ClassSectionRepository;
import com.fpt.repository.EnrollmentRepository;
import com.fpt.repository.SemesterRepository;
import com.fpt.util.InputUtil;
import com.fpt.util.Session;

import java.time.LocalTime;
import java.util.List;

public class EnrollmentMenu {

    private static final String BACK_OPTION = "0";

    private final SemesterRepository semesterRepository =
            new SemesterRepository();
    private final ClassSectionRepository classSectionRepository =
            new ClassSectionRepository();
    private final EnrollmentRepository enrollmentRepository =
            new EnrollmentRepository();

    public void show() {
        Student student = Session.getCurrentStudent();

        if (student == null) {
            System.out.println("Session expired. Please login again.");
            return;
        }

        Semester activeSemester = semesterRepository.findActiveSemester();

        if (activeSemester == null) {
            System.out.println("No active semester. Enrollment is currently closed.");
            return;
        }

        System.out.println("\nActive semester: " + activeSemester.getSemesterCode());
        showCourseInputFlow(student, activeSemester);
    }

    private void showCourseInputFlow(Student student, Semester semester) {
        while (true) {
            System.out.println("\n===== COURSE ENROLLMENT =====");

            List<Course> availableCourses =
                    classSectionRepository.findAvailableCoursesBySemester(
                            semester.getSemesterCode()
                    );

            printAvailableCourses(availableCourses);

            String courseCode = InputUtil.getString(
                    "Enter course code (0 to back): "
            );

            if (isBack(courseCode)) {
                return;
            }

            if (courseCode.isBlank()) {
                System.out.println("Course code cannot be empty.");
                continue;
            }

            List<ClassSection> allSections =
                    classSectionRepository.findAvailableBySemesterAndCourseCode(
                            semester.getSemesterCode(),
                            courseCode
                    );

            if (allSections.isEmpty()) {
                System.out.println("No available class sections for course: "
                        + courseCode.toUpperCase()
                        + " (not found or all sections are full).");
                continue;
            }

            List<Integer> enrolledIds =
                    enrollmentRepository.findEnrolledClassSectionIdsByStudentAndSemester(
                            student.getId(), semester.getId()
                    );

            printAvailableClassSections(allSections, enrolledIds);

            showClassSelectionFlow(student, semester, allSections, enrolledIds);
        }
    }

    private void showClassSelectionFlow(
            Student student,
            Semester semester,
            List<ClassSection> available,
            List<Integer> enrolledIds
    ) {
        while (true) {
            String classCode = InputUtil.getString(
                    "Enter class code to enroll (0 to back): "
            );

            if (isBack(classCode)) {
                return;
            }

            if (classCode.isBlank()) {
                System.out.println("Class code cannot be empty.");
                continue;
            }

            ClassSection selected = available.stream()
                    .filter(cs -> cs.getClassCode().equalsIgnoreCase(classCode))
                    .findFirst()
                    .orElse(null);

            if (selected == null) {
                System.out.println("Class code not found in the list above.");
                continue;
            }

            if (enrolledIds.contains(selected.getId())) {
                System.out.println("You are already enrolled in class: "
                        + classCode.toUpperCase());
                continue;
            }

            String conflict = findScheduleConflict(student, semester, selected);
            if (conflict != null) {
                System.out.println("Cannot enroll — schedule conflict detected:");
                System.out.println("  >> " + conflict);
                continue;
            }

            boolean success = enrollmentRepository.enroll(student.getId(), selected.getId());

            if (success) {
                System.out.println("\n[SUCCESS] Enrolled in: "
                        + selected.getClassCode()
                        + " — " + selected.getCourse().getCourseName());
            } else {
                System.out.println("[FAILED] Enrollment failed. " +
                        "Section may now be full. Please try another class.");
            }

            return;
        }
    }

    private String findScheduleConflict(
            Student student,
            Semester semester,
            ClassSection target
    ) {
        List<Schedule> targetSchedules =
                classSectionRepository.findSchedulesByClassSectionId(target.getId());

        if (targetSchedules.isEmpty()) {
            return null;
        }

        List<Object[]> existingSchedules =
                enrollmentRepository.findScheduleByStudentAndSemester(
                        student.getId(), semester.getId()
                );

        for (Schedule ts : targetSchedules) {
            for (Object[] row : existingSchedules) {
                String existDay      = (String)    row[0];
                LocalTime existStart = (LocalTime) row[1];
                LocalTime existEnd   = (LocalTime) row[2];
                String existClass    = (String)    row[3];

                if (ts.getDayOfWeek().equals(existDay)
                        && isOverlapping(ts.getStartTime(), ts.getEndTime(),
                        existStart,        existEnd)) {

                    return existDay
                            + " " + existStart + "-" + existEnd
                            + " with class [" + existClass + "]";
                }
            }
        }

        return null;
    }

    private boolean isOverlapping(
            LocalTime s1, LocalTime e1,
            LocalTime s2, LocalTime e2
    ) {
        return s1.isBefore(e2) && s2.isBefore(e1);
    }


    private void printAvailableCourses(List<Course> courses) {
        System.out.println("\n===== AVAILABLE COURSES =====");
        System.out.printf("%-15s %-30s%n", "Course Code", "Course Name");
        System.out.println("----------------------------------------------");

        for (Course c : courses) {
            System.out.printf(
                    "%-15s %-30s%n",
                    c.getCourseCode(),
                    c.getCourseName()
            );
        }
    }

    private void printAvailableClassSections(
            List<ClassSection> sections,
            List<Integer> enrolledIds
    ) {
        System.out.println("\n===== AVAILABLE CLASS SECTIONS =====");
        System.out.printf(
                "%-15s %-25s %-10s %-8s %-12s%n",
                "Class Code", "Lecturer", "Room", "Slots", "Status"
        );
        System.out.println("----------------------------------------------------------------------");

        for (ClassSection cs : sections) {
            String status = enrolledIds.contains(cs.getId()) ? "[ENROLLED]" : "";
            System.out.printf(
                    "%-15s %-25s %-10s %d/%-7d %-12s%n",
                    cs.getClassCode(),
                    cs.getLecturer().getFullName(),
                    valueOrEmpty(cs.getRoom()),
                    valueOrZero(cs.getCurrentEnrolled()),
                    valueOrZero(cs.getMaxStudents()),
                    status
            );
        }
    }

    private boolean isBack(String input) {
        return BACK_OPTION.equals(input);
    }

    private String valueOrEmpty(String value) {
        return value == null ? "" : value;
    }

    private int valueOrZero(Integer value) {
        return value == null ? 0 : value;
    }
}