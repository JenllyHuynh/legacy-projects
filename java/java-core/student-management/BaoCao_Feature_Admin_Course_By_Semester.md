# Bao cao feature: Admin xem mon hoc theo semester va chi tiet lop

## 1. Muc tieu feature

Feature nay phuc vu man hinh Admin trong he thong University Management System.

Sau khi dang nhap voi vai tro Admin, nguoi dung co the:

1. Chon chuc nang **Xem danh sach khoa hoc**.
2. Nhap ma hoc ky, vi du: `SP26`.
3. He thong hien thi danh sach cac mon hoc co trong hoc ky do, vi du: `SWR`, `SWT`, `SWP`.
4. Admin chon mot mon hoc bang `course_code`.
5. He thong hien thi danh sach cac lop/class section cua mon hoc do trong hoc ky da chon, vi du: `SE1909`, `SE1910`.
6. Admin chon mot lop bang `class_code`.
7. He thong hien thi:
   - Giang vien phu trach lop.
   - Danh sach sinh vien dang o trong lop.

Feature chi tap trung vao viec **xem du lieu**. Chuc nang them, sua, xoa mon hoc/lop hoc khong nam trong scope cua feature nay.

## 2. Pham vi code da implement

Feature duoc implement trong cac package chinh:

- `com.fpt.ui`
- `com.fpt.repository`
- `com.fpt.entity`

Nhung file quan trong:

| File | Vai tro |
| --- | --- |
| `Main.java` | Entry point cua chuong trinh, goi `LoginMenu` |
| `LoginMenu.java` | Menu chon role Admin/Student |
| `AdminMenu.java` | Menu sau khi vao Admin, co option so `3` de vao feature |
| `CourseMenu.java` | Xu ly toan bo flow nhap semester, chon course, chon class section, hien thi lecturer va students |
| `SemesterRepository.java` | Tim semester theo `semester_code` |
| `CourseRepository.java` | Lay danh sach mon hoc trong semester |
| `ClassSectionRepository.java` | Lay danh sach lop cua mon hoc va lay chi tiet lop |
| `EnrollmentRepository.java` | Lay danh sach sinh vien trong lop |

## 3. Flow nguoi dung

### 3.1. Flow tong quat

```text
Run app
  -> Login menu
      -> Chon 1. Admin
          -> Admin menu
              -> Chon 3. Xem danh sach khoa hoc
                  -> Nhap semester_code
                      -> Show danh sach course trong semester
                          -> Nhap course_code
                              -> Show danh sach class section cua course
                                  -> Nhap class_code
                                      -> Show lecturer
                                      -> Show danh sach student
```

### 3.2. Flow chi tiet tren console

Khi chay app, menu dau tien:

```text
===== LOGIN SYSTEM =====
1. Admin
2. Student
0. Exit
Select Role to login:
```

Nhap:

```text
1
```

He thong vao Admin menu:

```text
===== ADMIN MENU =====
1. Xem danh sach sinh vien
2. Xem danh sach giang vien
3. Xem danh sach khoa hoc
0. Logout
Select option:
```

Nhap:

```text
3
```

He thong yeu cau nhap semester:

```text
===== COURSES BY SEMESTER =====
Enter semester code (0 to back):
```

Nhap vi du:

```text
SP26
```

Neu semester ton tai va co lop hoc, he thong hien danh sach mon:

```text
===== COURSE LIST =====
Course Code     Course Name                              Credits
-------------------------------------------------------------------
SWR             Software Requirement                     3
SWT             Software Testing                         3
```

Sau do he thong yeu cau nhap ma mon:

```text
Enter course code from the list (0 to back):
```

Nhap:

```text
SWR
```

He thong hien danh sach lop cua mon `SWR` trong semester `SP26`:

```text
===== CLASS SECTION LIST =====
Class Code      Room            Enrolled
---------------------------------------------
SE1909          R101            2/30
SE1910          R102            0/30
```

Sau do he thong yeu cau nhap ma lop:

```text
Enter class code from the list (0 to back):
```

Nhap:

```text
SE1909
```

He thong hien giang vien:

```text
===== LECTURER =====
Code            Full Name                      Email                          Department
-----------------------------------------------------------------------------------------------
GV001           Nguyen Van A                   a@fpt.edu.vn                   Software Engineering
```

He thong hien danh sach sinh vien:

```text
===== STUDENT LIST =====
Student Code    Full Name                      Email                          Phone
------------------------------------------------------------------------------------------
SE001           Tran Van B                     b@fpt.edu.vn                   0901111111
SE002           Le Thi C                       c@fpt.edu.vn                   0902222222
```

## 4. Luong code va giai thich de bao tri

### 4.1. `Main.java`

`Main.java` la diem chay dau tien cua ung dung.

Hien tai `main()` se goi:

```java
new LoginMenu().showFormLogin();
```

Sau khi chuong trinh ket thuc, `JpaUtil.shutdown()` duoc goi trong `finally` de dong `EntityManagerFactory`.

Y nghia:

- Dam bao khi chay app that thi nguoi dung vao duoc menu login.
- Feature Admin co the demo duoc tu flow that cua app, khong phai goi truc tiep `CourseMenu`.

### 4.2. `LoginMenu.java`

`LoginMenu.showFormLogin()` hien menu chon role:

- `1. Admin`
- `2. Student`
- `0. Exit`

Neu chon `1`, code se goi:

```java
new AdminMenu().adminMenu();
```

Chuc nang Student chua nam trong scope feature nay nen chi in thong bao:

```text
Student menu is not implemented in this task.
```

### 4.3. `AdminMenu.java`

`AdminMenu.adminMenu()` hien menu Admin:

```text
1. Xem danh sach sinh vien
2. Xem danh sach giang vien
3. Xem danh sach khoa hoc
0. Logout
```

Option quan trong cua feature la option `3`.

Khi admin chon `3`, code goi:

```java
courseMenu.showCoursesBySemesterFlow();
```

Hai option `1` va `2` hien tai chua implement vi khong thuoc task nay.

### 4.4. `CourseMenu.java`

`CourseMenu` la file xu ly chinh cua feature.

#### Method `showCoursesBySemesterFlow()`

Trach nhiem:

- Yeu cau admin nhap `semester_code`.
- Kiem tra semester co ton tai hay khong.
- Lay danh sach course trong semester.
- In danh sach course.
- Chuyen sang buoc chon course.

Repository duoc goi:

```java
semesterRepository.findByCode(semesterCode);
courseRepository.findBySemesterCode(semesterCode);
```

Neu semester khong ton tai:

```text
Semester not found.
```

Neu semester ton tai nhung khong co mon nao:

```text
No courses found in this semester.
```

#### Method `showClassSectionsByCourseFlow(String semesterCode)`

Trach nhiem:

- Yeu cau admin nhap `course_code`.
- Lay danh sach class section cua course do trong semester da chon.
- In danh sach lop.
- Chuyen sang buoc chon lop.

Repository duoc goi:

```java
classSectionRepository.findBySemesterAndCourseCode(semesterCode, courseCode);
```

Neu course khong co class section trong semester:

```text
No class sections found for this course in the semester.
```

#### Method `showClassSectionDetailFlow(String semesterCode, String courseCode)`

Trach nhiem:

- Yeu cau admin nhap `class_code`.
- Lay dung class section da chon.
- In thong tin lecturer.
- Lay danh sach sinh vien trong lop.
- In danh sach sinh vien.

Repository duoc goi:

```java
classSectionRepository.findBySemesterCourseAndClassCode(
        semesterCode,
        courseCode,
        classCode
);

enrollmentRepository.findActiveStudentsByClassSectionId(
        classSection.getId()
);
```

Neu class section khong ton tai:

```text
Class section not found.
```

Neu lop khong co sinh vien:

```text
No students found in this class section.
```

### 4.5. Cac method in du lieu trong `CourseMenu`

`printCourses(List<Course> courses)`

- In danh sach mon hoc.
- Cac cot hien thi:
  - `Course Code`
  - `Course Name`
  - `Credits`

`printClassSections(List<ClassSection> classSections)`

- In danh sach lop cua mon hoc.
- Cac cot hien thi:
  - `Class Code`
  - `Room`
  - `Enrolled`

`printLecturer(Lecturer lecturer)`

- In thong tin giang vien.
- Cac cot hien thi:
  - `Code`
  - `Full Name`
  - `Email`
  - `Department`

`printStudents(List<Student> students)`

- In danh sach sinh vien trong lop.
- Cac cot hien thi:
  - `Student Code`
  - `Full Name`
  - `Email`
  - `Phone`

## 5. Giai thich cac repository query

### 5.1. `SemesterRepository.findByCode(String semesterCode)`

Muc dich:

- Tim semester theo `semester_code`.
- So sanh khong phan biet chu hoa/chu thuong bang `upper(...)`.

Neu tim thay thi tra ve `Semester`.

Neu khong tim thay thi tra ve `null`.

### 5.2. `CourseRepository.findBySemesterCode(String semesterCode)`

Muc dich:

- Lay danh sach mon hoc co class section trong semester da nhap.

Logic:

- Tu bang `ClassSection`.
- Join sang `Course`.
- Join sang `Semester`.
- Loc theo `semesterCode`.
- Dung `distinct` de neu mot mon co nhieu lop thi mon do chi hien thi mot lan.

Vi du:

Neu `SWR` co 2 lop `SE1909`, `SE1910`, danh sach course van chi hien:

```text
SWR
```

### 5.3. `ClassSectionRepository.findBySemesterAndCourseCode(...)`

Muc dich:

- Lay danh sach lop cua mot mon trong mot semester.

Tham so:

- `semesterCode`
- `courseCode`

Ket qua:

- Danh sach `ClassSection`.

Query co dung `join fetch` voi:

- `course`
- `semester`
- `lecturer`

Ly do:

- Entity `ClassSection` dang mapping `course`, `semester`, `lecturer` la `LAZY`.
- Neu dong `EntityManager` xong moi truy cap lecturer/course thi co the gap loi lazy loading.
- `join fetch` giup lay san du lieu can dung truoc khi dong `EntityManager`.

### 5.4. `ClassSectionRepository.findBySemesterCourseAndClassCode(...)`

Muc dich:

- Lay dung mot lop ma admin chon.

Tham so:

- `semesterCode`
- `courseCode`
- `classCode`

Ket qua:

- Tra ve `ClassSection` neu tim thay.
- Tra ve `null` neu khong tim thay.

Query cung dung `join fetch lecturer` de sau do `CourseMenu` co the in thong tin giang vien.

### 5.5. `EnrollmentRepository.findActiveStudentsByClassSectionId(Integer classSectionId)`

Muc dich:

- Lay danh sach sinh vien dang o trong mot lop.

Dieu kien loc:

```sql
status in ('pending', 'confirmed')
```

Nghia la:

- Lay sinh vien co enrollment `pending`.
- Lay sinh vien co enrollment `confirmed`.
- Khong lay sinh vien co enrollment `cancelled`.

Ly do:

- Theo yeu cau da chot, danh sach sinh vien trong lop la cac enrollment chua bi huy.

## 6. Quy tac nhap va dieu huong

Trong feature nay, admin nhap bang code:

| Du lieu | Truong DB dung de nhap | Vi du |
| --- | --- | --- |
| Semester | `semester_code` | `SP26` |
| Course | `course_code` | `SWR` |
| Class Section | `class_code` | `SE1909` |

O moi man hinh nhap, admin co the nhap:

```text
0
```

de quay lai man hinh truoc.

Thu tu lui menu:

```text
Chon class -> 0 -> quay lai chon course
Chon course -> 0 -> quay lai nhap semester
Nhap semester -> 0 -> quay lai Admin menu
Admin menu -> 0 -> quay lai Login menu
Login menu -> 0 -> thoat app
```

## 7. Du lieu demo

Chay script SQL duoi day trong SQL Server Management Studio truoc khi demo.

Script nay tao:

- 1 semester: `SP26`
- 2 mon hoc: `SWR`, `SWT`
- 1 giang vien: `GV001`
- 3 sinh vien: `SE001`, `SE002`, `SE003`
- 3 lop:
  - `SE1909` cua mon `SWR`
  - `SE1910` cua mon `SWR`
  - `SE1920` cua mon `SWT`
- Enrollment:
  - `SE001` la `confirmed`
  - `SE002` la `pending`
  - `SE003` la `cancelled`

```sql
USE StudentCourseManagement;

INSERT INTO Semester (semester_code, start_date, end_date, is_active)
VALUES ('SP26', '2026-01-01', '2026-05-31', 1);

DECLARE @semesterId INT = SCOPE_IDENTITY();

INSERT INTO Course (course_code, course_name, credits)
VALUES
('SWR', N'Software Requirement', 3),
('SWT', N'Software Testing', 3);

DECLARE @swrId INT = (SELECT course_id FROM Course WHERE course_code = 'SWR');
DECLARE @swtId INT = (SELECT course_id FROM Course WHERE course_code = 'SWT');

INSERT INTO Lecturer (lecturer_code, full_name, email, department)
VALUES ('GV001', N'Nguyen Van A', 'a@fpt.edu.vn', N'Software Engineering');

DECLARE @lecturerId INT = SCOPE_IDENTITY();

INSERT INTO Student (student_code, full_name, email, password_hash, phone)
VALUES
('SE001', N'Tran Van B', 'b@fpt.edu.vn', '123', '0901111111'),
('SE002', N'Le Thi C', 'c@fpt.edu.vn', '123', '0902222222'),
('SE003', N'Pham Van D Cancelled', 'd@fpt.edu.vn', '123', '0903333333');

INSERT INTO ClassSection (
    class_code,
    course_id,
    semester_id,
    lecturer_id,
    max_students,
    room,
    current_enrolled,
    version
)
VALUES
('SE1909', @swrId, @semesterId, @lecturerId, 30, 'R101', 2, 0),
('SE1910', @swrId, @semesterId, @lecturerId, 30, 'R102', 0, 0),
('SE1920', @swtId, @semesterId, @lecturerId, 30, 'R201', 0, 0);

DECLARE @classId INT = (
    SELECT class_section_id
    FROM ClassSection
    WHERE class_code = 'SE1909'
);

INSERT INTO Enrollment (student_id, class_section_id, status)
SELECT student_id, @classId, 'confirmed'
FROM Student
WHERE student_code = 'SE001';

INSERT INTO Enrollment (student_id, class_section_id, status)
SELECT student_id, @classId, 'pending'
FROM Student
WHERE student_code = 'SE002';

INSERT INTO Enrollment (student_id, class_section_id, status)
SELECT student_id, @classId, 'cancelled'
FROM Student
WHERE student_code = 'SE003';
```

## 8. Cach chay app de demo

Mo terminal tai thu muc project:

```powershell
cd C:\Users\HP\Desktop\LAB302
```

Chay lenh:

```powershell
mvn compile dependency:build-classpath "-Dmdep.outputFile=target\classpath.txt"
$cp = Get-Content target\classpath.txt
java -cp "target\classes;$cp" com.fpt.Main
```

Neu project sau nay co cau hinh Maven exec plugin thi co the chay ngan hon:

```powershell
mvn exec:java -Dexec.mainClass="com.fpt.Main"
```

## 9. Kich ban test tay

### Test case 1: Happy path - xem lop co sinh vien

Muc tieu:

- Kiem tra flow chinh cua feature.
- Dam bao admin xem duoc course list, class list, lecturer va student list.

Input lan luot:

```text
1
3
SP26
SWR
SE1909
```

Ket qua mong doi:

1. Sau khi nhap `SP26`, he thong hien:

```text
SWR
SWT
```

2. Sau khi nhap `SWR`, he thong hien:

```text
SE1909
SE1910
```

3. Sau khi nhap `SE1909`, he thong hien lecturer:

```text
GV001
Nguyen Van A
a@fpt.edu.vn
Software Engineering
```

4. He thong hien sinh vien:

```text
SE001
Tran Van B

SE002
Le Thi C
```

5. He thong khong hien `SE003` vi sinh vien nay co enrollment status la `cancelled`.

### Test case 2: Lop khong co sinh vien

Muc tieu:

- Kiem tra truong hop lop ton tai nhung chua co sinh vien active.

Input:

```text
1
3
SP26
SWR
SE1910
```

Ket qua mong doi:

- He thong van hien lecturer `GV001`.
- Phan student list hien:

```text
No students found in this class section.
```

### Test case 3: Nhap semester sai

Muc tieu:

- Kiem tra xu ly khi semester khong ton tai.

Input:

```text
1
3
NOSEM
```

Ket qua mong doi:

```text
Semester not found.
```

Sau do he thong quay lai man nhap semester.

### Test case 4: Nhap course sai

Muc tieu:

- Kiem tra xu ly khi course khong co lop trong semester da chon.

Input:

```text
1
3
SP26
BADCOURSE
```

Ket qua mong doi:

```text
No class sections found for this course in the semester.
```

Sau do he thong quay lai man nhap course.

### Test case 5: Nhap class sai

Muc tieu:

- Kiem tra xu ly khi class code khong ton tai trong course va semester da chon.

Input:

```text
1
3
SP26
SWR
BADCLASS
```

Ket qua mong doi:

```text
Class section not found.
```

Sau do he thong quay lai man nhap class.

### Test case 6: Quay lai menu bang `0`

Muc tieu:

- Kiem tra dieu huong back.

Input:

```text
1
3
SP26
SWR
0
0
0
0
0
```

Ket qua mong doi:

- `0` o man nhap class: quay lai nhap course.
- `0` o man nhap course: quay lai nhap semester.
- `0` o man nhap semester: quay lai Admin menu.
- `0` o Admin menu: quay lai Login menu.
- `0` o Login menu: thoat chuong trinh.

## 10. Cac loi thuong gap khi demo

### 10.1. Khong thay du lieu course

Nguyen nhan co the:

- Chua chay script SQL demo.
- Nhap sai `semester_code`.
- Course ton tai nhung chua co record trong `ClassSection`.

Cach kiem tra nhanh:

```sql
SELECT * FROM Semester WHERE semester_code = 'SP26';

SELECT s.semester_code, c.course_code, cs.class_code
FROM ClassSection cs
JOIN Semester s ON cs.semester_id = s.semester_id
JOIN Course c ON cs.course_id = c.course_id
WHERE s.semester_code = 'SP26';
```

### 10.2. Thay nhieu dong `Hibernate: select ...`

Day khong phai loi.

Nguyen nhan la trong `persistence.xml` dang bat:

```xml
<property name="hibernate.show_sql" value="true"/>
```

Neu muon console gon hon khi demo, co the doi thanh:

```xml
<property name="hibernate.show_sql" value="false"/>
```

Tuy nhien, viec nay khong bat buoc.

### 10.3. Loi ket noi SQL Server

Kiem tra file:

```text
src/main/resources/META-INF/persistence.xml
```

Thong tin hien tai:

```xml
<property name="jakarta.persistence.jdbc.url"
          value="jdbc:sqlserver://localhost:1433;databaseName=StudentCourseManagement;encrypt=false;"/>
<property name="jakarta.persistence.jdbc.user" value="sa"/>
<property name="jakarta.persistence.jdbc.password" value="123456"/>
```

Can dam bao:

- SQL Server dang chay.
- Port la `1433`.
- Database ten dung la `StudentCourseManagement`.
- Tai khoan `sa` va password `123456` dang dung.

### 10.4. Loi trung du lieu khi chay script demo nhieu lan

Script demo o tren khong xoa du lieu cu truoc khi insert.

Neu da chay script demo mot lan, lan sau co the bi loi trung unique key, vi cac code nhu `SP26`, `SWR`, `SE1909`, `SE001` da ton tai.

Co the xoa du lieu demo bang script:

```sql
USE StudentCourseManagement;

DELETE e
FROM Enrollment e
JOIN Student st ON e.student_id = st.student_id
WHERE st.student_code IN ('SE001', 'SE002', 'SE003');

DELETE e
FROM Enrollment e
JOIN ClassSection cs ON e.class_section_id = cs.class_section_id
WHERE cs.class_code IN ('SE1909', 'SE1910', 'SE1920');

DELETE FROM ClassSection
WHERE class_code IN ('SE1909', 'SE1910', 'SE1920');

DELETE FROM Course
WHERE course_code IN ('SWR', 'SWT');

DELETE FROM Semester
WHERE semester_code = 'SP26';

DELETE FROM Lecturer
WHERE lecturer_code = 'GV001';

DELETE FROM Student
WHERE student_code IN ('SE001', 'SE002', 'SE003');
```

## 11. Ghi chu bao tri

1. Neu sau nay them login that cho Admin, flow `LoginMenu -> AdminMenu -> CourseMenu` van co the giu nguyen. Chi can them buoc validate username/password truoc khi goi `AdminMenu`.

2. Neu sau nay muon hien ca lich hoc cua lop, co the them query trong `ScheduleRepository` theo `classSectionId`, roi in them o man chi tiet lop.

3. Neu sau nay doi yeu cau danh sach sinh vien chi hien `confirmed`, can sua dieu kien trong:

```java
EnrollmentRepository.findActiveStudentsByClassSectionId(...)
```

Hien tai dang lay:

```text
pending
confirmed
```

va bo qua:

```text
cancelled
```

4. Cac entity `ClassSection.course`, `ClassSection.semester`, `ClassSection.lecturer` dang dung `FetchType.LAZY`, vi vay khi repository tra object ve UI nen dung `join fetch` voi cac relation can in ra.

5. Feature nay khong cap nhat `current_enrolled`. Truong `current_enrolled` chi duoc in ra tu DB. Neu du lieu trong DB sai, console cung se hien sai theo DB.

## 12. Tom tat nhanh cho dong nghiep

Feature nay nam chu yeu trong `CourseMenu`.

Flow:

```text
AdminMenu option 3
  -> CourseMenu.showCoursesBySemesterFlow()
      -> CourseRepository.findBySemesterCode()
      -> ClassSectionRepository.findBySemesterAndCourseCode()
      -> ClassSectionRepository.findBySemesterCourseAndClassCode()
      -> EnrollmentRepository.findActiveStudentsByClassSectionId()
```

Input dung code:

```text
semester_code -> course_code -> class_code
```

Output:

```text
Course list -> Class section list -> Lecturer -> Student list
```

Enrollment duoc hien:

```text
pending, confirmed
```

Enrollment bi an:

```text
cancelled
```
