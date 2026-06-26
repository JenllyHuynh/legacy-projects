package com.fpt.ui;

import com.fpt.entity.ClassSection;
import com.fpt.entity.Lecturer;
import com.fpt.entity.Schedule; // <--- Thêm dòng này để import thực thể Schedule
import com.fpt.repository.ClassSectionRepository;
import com.fpt.repository.LecturerRepository;
import com.fpt.repository.ScheduleRepository;
import com.fpt.util.InputUtil;

import java.util.List;

public class ClassSectionMenu {

    private final ClassSectionRepository classRepo = new ClassSectionRepository();
    private final LecturerRepository lecturerRepo = new LecturerRepository();
    private final ScheduleRepository scheduleRepo = new ScheduleRepository();

    public void manageClassSectionFlow() {
        while (true) {
            System.out.println("\n===== UPDATE/DELETE CLASS SECTION =====");

            String courseCode = InputUtil.getString("Nhap ma mon hoc (0 de thoat): ");
            if (courseCode.equals("0")) return;
            if (courseCode.isBlank()) continue;

            List<ClassSection> classes = classRepo.findByCourseCode(courseCode);
            if (classes.isEmpty()) {
                System.out.println("Khong co lop nao cho mon hoc nay!");
                continue;
            }

            System.out.println("\n--- DANH SACH LOP HOC PHAN MON " + courseCode.toUpperCase() + " ---");
            System.out.printf("%-5s %-15s %-25s %-15s %-20s%n", "ID", "Class Code", "Lecturer", "Room", "Lich hoc");
            System.out.println("--------------------------------------------------------------------------------------");
            for (ClassSection cs : classes) {
                // Code bây giờ đã ngắn gọn và sạch đẹp hơn rất nhiều
                List<Schedule> schedules = scheduleRepo.findByClassSectionId(cs.getId());
                StringBuilder scheduleBuilder = new StringBuilder();
                for (Schedule s : schedules) {
                    if (scheduleBuilder.length() > 0) scheduleBuilder.append(", ");
                    scheduleBuilder.append(s.getDayOfWeek()).append(" (").append(s.getStartTime()).append("-").append(s.getEndTime()).append(")");
                }
                String lichHoc = scheduleBuilder.length() == 0 ? "Chua sep lich" : scheduleBuilder.toString();

                System.out.printf("%-5d %-15s %-25s %-15s %-20s%n",
                        cs.getId(), cs.getClassCode(), cs.getLecturer().getFullName(),
                        cs.getRoom() == null ? "N/A" : cs.getRoom(), lichHoc);
            }

            String classInput = InputUtil.getString("\nNhap ID hoac Ma lop can thao tac (0 de quay lai): ");
            if (classInput.equals("0")) continue;

            ClassSection targetClass = classRepo.findByClassCodeOrId(classInput);
            if (targetClass == null) {
                System.out.println("(!) Khong tim thay lop hoc nay.");
                continue;
            }

            System.out.println("\nDa chon lop: " + targetClass.getClassCode());
            System.out.println("1. Cap nhat thong tin lop (Giang vien, Lich/Phong)");
            System.out.println("2. Xoa lop hoc nay");
            System.out.println("0. Huy");

            int choice = InputUtil.getInt("Chon chuc nang: ");
            if (choice == 1) {
                updateClass(targetClass);
            } else if (choice == 2) {
                deleteClass(targetClass);
            }
        }
    }

    private void updateClass(ClassSection cs) {
        System.out.println("\n--- CAP NHAT THONG TIN --- (Nhan Enter de giu nguyen thong tin cu)");

        // 1. Cập nhật Giảng viên
        String curLec = cs.getLecturer().getLecturerCode() + " - " + cs.getLecturer().getFullName();
        String newLecCode = InputUtil.getString("Ma Giang vien moi (Hien tai: " + curLec + "): ");
        if (!newLecCode.isBlank()) {
            Lecturer newLecturer = lecturerRepo.findByCode(newLecCode);
            if (newLecturer != null) {
                cs.setLecturer(newLecturer);
            } else {
                System.out.println("Khong tim thay Giang vien! Giu nguyen giang vien cu.");
            }
        }

        // 2. Cập nhật Phòng học (Cột room trong bảng ClassSection)
        String curRoom = cs.getRoom() == null ? "Chua co" : cs.getRoom();
        String newRoom = InputUtil.getString("Phong hoc moi (Hien tai: " + curRoom + "): ");
        if (!newRoom.isBlank()) {
            cs.setRoom(newRoom);
        }

        // Lưu thông tin ClassSection trước
        try {
            classRepo.update(cs);
        } catch (Exception e) {
            System.out.println("[FAILED] Loi khi cap nhat lop: " + e.getMessage());
            return;
        }

        // 3. Cập nhật Lịch học (Tác động sang bảng Schedule)
        System.out.println("\n--- THAY DOI LICH HOC (SCHEDULE) ---");
        boolean changeSchedule = InputUtil.getBoolean("Ban co muon thiet lap lai Lich hoc cho lop nay khong?");
        if (changeSchedule) {
            java.util.ArrayList<Schedule> newSchedules = new java.util.ArrayList<>();
            int subChoice = 1;

            while (subChoice == 1) {
                System.out.println("\nNhap thong tin buoi hoc moi:");
                String day = InputUtil.getString("Nhap thu (VD: Mon, Tue, Wed, Thu, Fri, Sat, Sun): ");
                String start = InputUtil.getString("Gio bat dau (VD: 07:30:00): ");
                String end = InputUtil.getString("Gio ket thuc (VD: 09:00:00): ");

                try {
                    Schedule s = new Schedule();
                    s.setClassSection(cs);
                    s.setDayOfWeek(day);
                    s.setStartTime(java.time.LocalTime.parse(start));
                    s.setEndTime(java.time.LocalTime.parse(end));
                    s.setRoom(cs.getRoom()); // Lấy luôn phòng học vừa cập nhật ở trên

                    newSchedules.add(s);
                    System.out.println("[+] Da ghi nhan buoi hoc: " + day + " (" + start + " - " + end + ")");
                } catch (Exception e) {
                    System.out.println("(!) Dinh dang gio giac khong dung (Phai du hh:mm:ss). Vui long nhap lai buoi nay.");
                }

                System.out.println("1. Nhap tiep buoi nua cho lop");
                System.out.println("2. Hoan thanh va luu lich");
                subChoice = InputUtil.getInt("Chon option: ");
            }

            // Bắn dữ liệu xuống lưu ở bảng Schedule
            if (!newSchedules.isEmpty()) {
                try {
                    scheduleRepo.updateClassSchedules(cs.getId(), newSchedules);
                    System.out.println("[SUCCESS] Da cap nhat toan bo lich hoc moi xuong DB!");
                } catch (Exception e) {
                    System.out.println("[FAILED] Loi khi luu lich hoc: " + e.getMessage());
                }
            }
        } else {
            System.out.println("[INFO] Giu nguyen lich hoc cu.");
        }

        System.out.println("\n[SUCCESS] Hoan tat quy trinh cap nhat thong tin lop!");
    }

    private void deleteClass(ClassSection cs) {
        boolean confirm = InputUtil.getBoolean("Ban co chac chan muon xoa lop " + cs.getClassCode() + " khong?");
        if (confirm) {
            try {
                classRepo.delete(cs.getId());
                System.out.println("[SUCCESS] Da xoa lop thanh cong!");
            } catch (Exception e) {
                System.out.println("[FAILED] Khong the xoa! (Co the lop nay da co sinh vien dang ky).");
            }
        }
    }
}