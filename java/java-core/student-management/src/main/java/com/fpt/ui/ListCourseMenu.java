package com.fpt.ui;

import com.fpt.entity.ClassSection;
import com.fpt.entity.Enrollment;
import com.fpt.entity.Schedule;
import com.fpt.entity.Student;
import com.fpt.repository.EnrollmentRepository;
import com.fpt.util.InputUtil;
import com.fpt.util.Session;

import java.util.List;

public class ListCourseMenu {

    private final EnrollmentRepository enrollmentRepo = new EnrollmentRepository();

    public void show(Student student) {
        List<Enrollment> list =
                enrollmentRepo.getMyEnrollments(Session.getCurrentStudent().getId()
                );

        System.out.println("\n===== DANH SACH MON DA DANG KY =====");

        if (list.isEmpty()) {
            System.out.println("Ban chua dang ky mon hoc nao.");
            InputUtil.getString("Nhan Enter de quay lai...");
            return;
        }

        // Header
        System.out.printf("%-4s %-12s %-30s %-4s %-20s %-30s%n",
                "STT", "Ma lop", "Ten mon", "TC", "Giang vien", "Lich hoc");
        System.out.println("-".repeat(106));

        // Rows
        int i = 1;
        for (Enrollment e : list) {
            ClassSection cs = e.getClassSection();

            String schedule = buildSchedule(cs.getSchedules());

            System.out.printf("%-4d %-12s %-30s %-4d %-20s %-30s%n",
                    i++,
                    cs.getClassCode(),
                    cs.getCourse().getCourseName(),
                    cs.getCourse().getCredits(),
                    cs.getLecturer().getFullName(),
                    schedule);
        }

        System.out.println("-".repeat(106));
        System.out.println("0. Back");
        InputUtil.getInt("Chon: ");
    }

    private String buildSchedule(List<Schedule> schedules) {
        if (schedules == null || schedules.isEmpty()) return "Chua co lich";

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < schedules.size(); i++) {
            Schedule s = schedules.get(i);
            sb.append(s.getDayOfWeek())
                    .append(" ")
                    .append(s.getStartTime())
                    .append("-")
                    .append(s.getEndTime());
            if (i < schedules.size() - 1) sb.append(" | ");
        }
        return sb.toString();
    }
}
