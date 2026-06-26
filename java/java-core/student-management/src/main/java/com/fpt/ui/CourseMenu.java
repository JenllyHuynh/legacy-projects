package com.fpt.ui;

import com.fpt.entity.ClassSection;
import com.fpt.entity.Course;
import com.fpt.entity.Lecturer;
import com.fpt.entity.Semester;
import com.fpt.entity.Student;
import com.fpt.repository.ClassSectionRepository;
import com.fpt.repository.CourseRepository;
import com.fpt.repository.EnrollmentRepository;
import com.fpt.repository.SemesterRepository;
import com.fpt.util.InputUtil;

import java.util.List;

public class CourseMenu {

    private static final String BACK_OPTION = "0";

    private final SemesterRepository semesterRepository =
            new SemesterRepository();
    private final CourseRepository courseRepository =
            new CourseRepository();
    private final ClassSectionRepository classSectionRepository =
            new ClassSectionRepository();
    private final EnrollmentRepository enrollmentRepository =
            new EnrollmentRepository();

    public void showCoursesBySemesterFlow() {
        while (true) {
            System.out.println("\n===== COURSES BY SEMESTER =====");
            String semesterCode = InputUtil.getString(
                    "Enter semester code (0 to back): "
            );

            if (isBack(semesterCode)) {
                return;
            }

            if (semesterCode.isBlank()) {
                System.out.println("Semester code cannot be empty.");
                continue;
            }

            Semester semester = semesterRepository.findByCode(semesterCode);

            if (semester == null) {
                System.out.println("Semester not found.");
                continue;
            }

            List<Course> courses =
                    courseRepository.findBySemesterCode(semesterCode);

            if (courses.isEmpty()) {
                System.out.println("No courses found in this semester.");
                continue;
            }

            printCourses(courses);
            showClassSectionsByCourseFlow(semester.getSemesterCode());
        }
    }

    private void showClassSectionsByCourseFlow(String semesterCode) {
        while (true) {
            String courseCode = InputUtil.getString(
                    "Enter course code from the list (0 to back): "
            );

            if (isBack(courseCode)) {
                return;
            }

            if (courseCode.isBlank()) {
                System.out.println("Course code cannot be empty.");
                continue;
            }

            List<ClassSection> classSections =
                    classSectionRepository.findBySemesterAndCourseCode(
                            semesterCode,
                            courseCode
                    );

            if (classSections.isEmpty()) {
                System.out.println("No class sections found for this course in the semester.");
                continue;
            }

            printClassSections(classSections);
            showClassSectionDetailFlow(semesterCode, courseCode);
        }
    }

    private void showClassSectionDetailFlow(
            String semesterCode,
            String courseCode
    ) {
        while (true) {
            String classCode = InputUtil.getString(
                    "Enter class code from the list (0 to back): "
            );

            if (isBack(classCode)) {
                return;
            }

            if (classCode.isBlank()) {
                System.out.println("Class code cannot be empty.");
                continue;
            }

            ClassSection classSection =
                    classSectionRepository.findBySemesterCourseAndClassCode(
                            semesterCode,
                            courseCode,
                            classCode
                    );

            if (classSection == null) {
                System.out.println("Class section not found.");
                continue;
            }

            printLecturer(classSection.getLecturer());

            List<Student> students =
                    enrollmentRepository.findActiveStudentsByClassSectionId(
                            classSection.getId()
                    );

            printStudents(students);
        }
    }

    private void printCourses(List<Course> courses) {
        System.out.println("\n===== COURSE LIST =====");
        System.out.printf(
                "%-15s %-40s %-10s%n",
                "Course Code",
                "Course Name",
                "Credits"
        );
        System.out.println("-------------------------------------------------------------------");

        for (Course course : courses) {
            System.out.printf(
                    "%-15s %-40s %-10d%n",
                    course.getCourseCode(),
                    course.getCourseName(),
                    course.getCredits()
            );
        }
    }

    private void printClassSections(List<ClassSection> classSections) {
        System.out.println("\n===== CLASS SECTION LIST =====");
        System.out.printf(
                "%-15s %-15s %-15s%n",
                "Class Code",
                "Room",
                "Enrolled"
        );
        System.out.println("---------------------------------------------");

        for (ClassSection classSection : classSections) {
            System.out.printf(
                    "%-15s %-15s %d/%d%n",
                    classSection.getClassCode(),
                    valueOrEmpty(classSection.getRoom()),
                    valueOrZero(classSection.getCurrentEnrolled()),
                    valueOrZero(classSection.getMaxStudents())
            );
        }
    }

    private void printLecturer(Lecturer lecturer) {
        System.out.println("\n===== LECTURER =====");
        System.out.printf(
                "%-15s %-30s %-30s %-20s%n",
                "Code",
                "Full Name",
                "Email",
                "Department"
        );
        System.out.println("-----------------------------------------------------------------------------------------------");
        System.out.printf(
                "%-15s %-30s %-30s %-20s%n",
                lecturer.getLecturerCode(),
                lecturer.getFullName(),
                valueOrEmpty(lecturer.getEmail()),
                valueOrEmpty(lecturer.getDepartment())
        );
    }

    private void printStudents(List<Student> students) {
        System.out.println("\n===== STUDENT LIST =====");

        if (students.isEmpty()) {
            System.out.println("No students found in this class section.");
            return;
        }

        System.out.printf(
                "%-15s %-30s %-30s %-15s%n",
                "Student Code",
                "Full Name",
                "Email",
                "Phone"
        );
        System.out.println("------------------------------------------------------------------------------------------");

        for (Student student : students) {
            System.out.printf(
                    "%-15s %-30s %-30s %-15s%n",
                    student.getStudentCode(),
                    student.getFullName(),
                    valueOrEmpty(student.getEmail()),
                    valueOrEmpty(student.getPhone())
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
