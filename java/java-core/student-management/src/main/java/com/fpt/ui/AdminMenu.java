package com.fpt.ui;

import java.util.Map;

import com.fpt.dto.ClassSectionDTO;
import com.fpt.repository.ClassSectionRepository;
import com.fpt.repository.CourseRepository;
import com.fpt.repository.LecturerRepository;
import com.fpt.repository.SemesterRepository;
import com.fpt.util.InputUtil;
import com.fpt.entity.Course;
import com.fpt.entity.Lecturer;
import com.fpt.entity.Student;
import com.fpt.repository.StudentRepository;
import com.fpt.util.Session;
import java.util.Scanner;

import java.util.List;
import java.util.Scanner;

public class AdminMenu {
    private LecturerRepository lecturerRepository = new LecturerRepository();
    private StudentRepository studentRepository = new StudentRepository();
    private CourseRepository courseRepository = new CourseRepository();

    private final ClassSectionMenu classSectionMenu = new ClassSectionMenu();
    private final CourseMenu courseMenu = new CourseMenu();
    private final Scanner scanner = new Scanner(System.in);
    private final CreateCourseMenu createCourseMenu = new CreateCourseMenu(scanner);

    public void showMenu() {

        while (true) {
            System.out.println("\n================= ADMIN MENU =================");
            System.out.println("1. View list Student");
            System.out.println("2. View list Lecturer");
            System.out.println("3. View list course");
            System.out.println("4. Create new Class Section");
            System.out.println("5. Update/Delete Class Section");
            System.out.println("6. Create new Course");
            System.out.println("7. Logout");

            int input;
            while (true) {
                input = InputUtil.getInt("Choice number 1 to 7: ");

                if (input >= 1 && input <= 7) {
                    break;
                } else {
                    System.out.println("Selection Error ! Please choice agin 1 to 7.");
                }

            }

            switch (input) {
                case 1:
                    handleShowAllStudents();
                    break;
                case 2:
                    handleShowAllLecturers();
                    break;
                case 3:
                    courseMenu.showCoursesBySemesterFlow();
                    break;
                case 4:
                    this.createClassSectionClient();
                    break;
                case 5:
                    classSectionMenu.manageClassSectionFlow();
                    break;
                case 6:
                    createCourseMenu.show();
                    break;
                case 7:
                    Session.clear();
                    return;
            }
        }

    }

    public void createClassSectionClient() {
        var classCode = InputUtil.getString("Class code: ");
        var courseRepository = new CourseRepository();
        var courses = courseRepository.fetchAllDataForUI();
        int courseId;
        while (true) {
            System.out.println("--- Available Courses ---");
            for (Map.Entry<Integer, String> entry : courses.entrySet()) {
                System.out.println("[" + entry.getKey() + "] " + entry.getValue());
            }

            courseId = InputUtil.getInt("Choose course (Enter ID): ");

            if (courses.containsKey(courseId)) {
                break;
            }
            System.out.println("Invalid Course ID! Please try again.");
        }
        var semesterRepository = new SemesterRepository();
        var semesters = semesterRepository.fetchAllDataForUI();
        int semesterId;
        while (true) {
            System.out.println("--- Available Semesters ---");
            for (Map.Entry<Integer, String> entry : semesters.entrySet()) {
                System.out.println("[" + entry.getKey() + "] " + entry.getValue());
            }
            semesterId = InputUtil.getInt("Choose semester (Enter ID): ");
            if (semesters.containsKey(semesterId)) {
                break;
            }
            System.out.println("Invalid Semester ID! Please try again.");
        }
        var lecturerRepository = new LecturerRepository();
        var lectures = lecturerRepository.fetchAllDataForUI();
        int lecturesId;
        while (true) {
            System.out.println("--- Available Lecturers ---");
            for (Map.Entry<Integer, String> entry : lectures.entrySet()) {
                System.out.println("[" + entry.getKey() + "] " + entry.getValue());
            }
            lecturesId = InputUtil.getInt("Choose lecturer (Enter ID): ");
            if (lectures.containsKey(lecturesId)) {
                break;
            }
            System.out.println("Invalid Lecturer ID! Please try again.");
        }
        int maxStudents;
        while (true) {
            maxStudents = InputUtil.getInt("Max students(Max 40): ");
            if (maxStudents > 40) {
                System.out.println("Limit student 40!");
            } else if (maxStudents <= 0) {
                System.out.println("Max students must be positive number!");

            } else {
                break;
            }

        }

        var room = InputUtil.getString("Room: ");
        int currentEnrolled;
        while (true) {
            currentEnrolled = InputUtil.getInt("Current Enrolled: ");
            if (currentEnrolled <= 0) {
                System.out.println("Current enrolled must be positive number!");

            } else {
                break;
            }
        }
        int version;
        while (true) {
            version = InputUtil.getInt("Version: ");
            if (version <= 0) {
                System.out.println("Version must be positive number!");
            } else {
                break;
            }
        }
        var dto = new ClassSectionDTO();
        dto.setClassCode(classCode);
        dto.setCourseId(courseId);
        dto.setSemesterId(semesterId);
        dto.setLecturerId(lecturesId);
        dto.setMaxStudents(maxStudents);
        dto.setRoom(room);
        dto.setCurrentEnrolled(currentEnrolled);
        dto.setVersion(version);
        var classSectionRepository = new ClassSectionRepository();
        classSectionRepository.create(dto);

    }

    private void handleShowAllLecturers() {
        System.out.println("\n=================================================================");
        System.out.println("                    List Lecturer                                 ");
        System.out.println("=================================================================");

        List<Lecturer> list = lecturerRepository.showListLecturer();

        if (list == null || list.isEmpty()) {
            System.out.println("List Empty.");
            return;
        }

        System.out.printf("%-5s %-15s %-30s %-20s\n", "ID", "CodeName", "NameLecturer", "Department");
        System.out.println("-----------------------------------------------------------------");

        for (Lecturer l : list) {
            System.out.printf("%-5d %-15s %-30s %-20s\n",
                    l.getId(),
                    l.getLecturerCode(),
                    l.getFullName(),
                    l.getDepartment());
        }
        System.out.println("=================================================================");

        System.out.print("\nEnter back to menu...");

    }

    private void handleShowAllStudents() {
        System.out
                .println("\n=========================================================================================");
        System.out
                .println("                                   List Student                                            ");
        System.out.println("=========================================================================================");

        List<Student> list = studentRepository.findAll();

        if (list == null || list.isEmpty()) {
            System.out.println("List Empty.");
            return;
        }

        System.out.printf("%-5s %-15s %-25s %-25s %-15s\n", "ID", "CodeStudent", "NameStudent", "Email", "Phone");
        System.out.println("-----------------------------------------------------------------------------------------");

        for (Student s : list) {
            System.out.printf("%-5d %-15s %-25s %-25s %-15s\n",
                    s.getId(),
                    s.getStudentCode(),
                    s.getFullName(),
                    s.getEmail() != null ? s.getEmail() : "N/A",
                    s.getPhone() != null ? s.getPhone() : "N/A");
        }
        System.out.println("=========================================================================================");

    }

    private void handleCourseManagementBranch() {
        Scanner scanner = new Scanner(System.in);
        String semester;

        while (true) {
            System.out.print("Enter the semester to manage (e.g., FA25, SP26): ");
            semester = scanner.nextLine().trim().toUpperCase();

            if (semester.isEmpty()) {
                System.out.println("Semester cannot be empty! Please try again.");
                continue;
            }

            // Chỉ cho phép nhập chữ cái (A-Z) và số (0-9), độ dài từ 2 đến 10 ký tự
            if (!semester.matches("^[A-Z0-9]{2,10}$")) {
                System.out.println("Invalid format! Only letters and numbers are allowed (e.g., FA25).");
                continue;
            }

            break;
        }

        List<Course> listCourses = courseRepository.findBySemesterCode(semester);

        if (listCourses == null || listCourses.isEmpty()) {
            System.out.println(" No courses found for semester: " + semester);
        } else {
            System.out.println("\n=========================================================");
            System.out.printf("               COURSE LIST FOR SEMESTER %-5s            \n", semester);
            System.out.println("=========================================================");
            System.out.printf("%-10s %-20s %-30s %-10s\n", "ID", "Course Code", "Course Name", "Credits");
            System.out.println("---------------------------------------------------------");

            for (Course c : listCourses) {
                System.out.printf("%-10d %-20s %-30s %-10d\n",
                        c.getId(),
                        c.getCourseCode(),
                        c.getCourseName(),
                        c.getCredits());
            }
            System.out.println("=========================================================");
        }
    }
}
