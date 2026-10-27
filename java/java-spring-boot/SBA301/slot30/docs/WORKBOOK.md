# Workbook — Chapter 18 Part A

Quy ước kết quả: đánh dấu `[PASS]` sau khi command và verification đúng; `[FAIL]` thì lưu lỗi/SQL state trước khi sửa. Các path đều tính từ project root.

## 1. Tạo/chuẩn hóa project structure

- **Mục tiêu:** persistence không nằm trong consumer.
- **Thao tác:** kiểm tra các package `entity/dao/repository/service/util/app`.
- **Code/config:** `src/main/java/fu/edu/sba301/ch18/`.
- **Command:** `rg --files src/main src/test`.
- **Verification:** `Main` chỉ compose/call `IStudentService`; HQL chỉ ở `StudentDAO`.
- **PASS/FAIL:** `[ ]` PASS khi không có Hibernate API trong future JavaFX controller.

## 2. Hibernate configuration

- **Mục tiêu:** kết nối `LibraryDB` không hard-code secret.
- **Thao tác:** nạp `DB_URL`, `DB_USER`, `DB_PASSWORD` từ environment.
- **Code/config:** `hibernate.cfg.xml`, `.env.example`, `Main.sessionFactory()`.
- **Command:** `Get-ChildItem Env:DB_URL,Env:DB_USER,Env:DB_PASSWORD` (không chụp/commit giá trị password).
- **Verification:** log Hibernate in database URL/dialect; `SELECT DB_NAME();` trả `LibraryDB`.
- **PASS/FAIL:** `[ ]` PASS khi connect được và Git không chứa credential thật.

## 3. SessionFactory utility

- **Mục tiêu:** build factory đúng một lần, đóng theo application lifecycle.
- **Thao tác:** composition root gọi `HibernateUtil.initialize(...)`; Spring đóng bean.
- **Code/config:** `util/HibernateUtil.java`, `app/Main.java`.
- **Command:** `$env:APP_DEMO_ENABLED='false'; mvn spring-boot:run`.
- **Verification:** log chỉ có một lần `Database info`; process kết thúc và pool đóng.
- **PASS/FAIL:** `[ ]` PASS khi không tạo factory trong DAO/service.

## 4. Student/Book entity mapping

- **Mục tiêu:** PK IDENTITY, email UNIQUE, field constraints và many-to-many đúng.
- **Thao tác:** đọc annotation và schema Hibernate sinh.
- **Code/config:** `entity/Student.java`, `entity/Book.java`.
- **Command:** `mvn -Dtest=StudentServiceIntegrationTest test`.
- **Verification:** test schema tạo `STUDENTS`, `BOOKS`, `STUDENT_BOOKS`.
- **PASS/FAIL:** `[ ]` PASS khi entity có no-arg constructor và mapping build được.

## 5. CREATE DAO

- **Mục tiêu:** INSERT atomic, ID được gán.
- **Thao tác:** `StudentDAO.save()` dùng `persist` trong `inTransaction`.
- **Code/config:** `dao/StudentDAO.java`.
- **Command:** `mvn -Dtest=StudentServiceIntegrationTest#c01_r01_r03_createReadAndListInDeterministicOrder test`.
- **Verification:** `SELECT TOP (20) * FROM dbo.STUDENTS ORDER BY id DESC;`.
- **PASS/FAIL:** `[ ]` PASS khi ID khác null và row tồn tại.

## 6. READ ALL DAO

- **Mục tiêu:** typed HQL và thứ tự ổn định.
- **Thao tác:** gọi `findAll()`.
- **Code/config:** `from Student s order by s.id`, result type `Student.class`.
- **Command:** chạy test C01/R03 ở bước 5.
- **Verification:** IDs trả về tăng dần.
- **PASS/FAIL:** `[ ]` PASS khi list typed, deterministic.

## 7. READ ONE DAO

- **Mục tiêu:** phân biệt found/missing không dùng null ở boundary.
- **Thao tác:** `findById()` bọc `session.get()` bằng `Optional`.
- **Code/config:** `StudentDAO.findById`.
- **Command:** `mvn -Dtest=StudentServiceIntegrationTest#r02_missingIdHasMeaningfulException test`.
- **Verification:** existing → present; missing → service `StudentNotFoundException`.
- **PASS/FAIL:** `[ ]` PASS khi không có `NullPointerException`.

## 8. SEARCH DAO

- **Mục tiêu:** parameterized typed HQL theo email.
- **Thao tác:** normalize email ở service, bind `:email` ở DAO.
- **Code/config:** `StudentDAO.findByEmail`.
- **Command:** chạy test C01/R03.
- **Verification:** input có khoảng trắng/chữ hoa vẫn tìm đúng normalized row.
- **PASS/FAIL:** `[ ]` PASS khi không nối chuỗi HQL.

## 9. UPDATE managed path

- **Mục tiêu:** quan sát dirty checking.
- **Thao tác:** load và `setMarks()` trong cùng Session/transaction; không gọi update.
- **Code/config:** `StudentDAO.updateMarks`.
- **Command:** `mvn -Dtest=StudentServiceIntegrationTest#u01_managedDirtyCheckingAndDetachedMergeBothUpdateTheRow test`.
- **Verification:** reload row có marks mới; SQL log có UPDATE khi commit.
- **PASS/FAIL:** `[ ]` PASS khi managed mutation được flush.

## 10. UPDATE detached path

- **Mục tiêu:** merge entity từ consumer/session cũ.
- **Thao tác:** gọi `session.merge(detached)` và dùng return value.
- **Code/config:** `StudentDAO.update`.
- **Command:** chạy test U01 ở bước 9.
- **Verification:** `mergeResult` khác object detached và DB có dữ liệu mới.
- **PASS/FAIL:** `[ ]` PASS khi không bỏ qua return của `merge()`.

## 11. DELETE DAO

- **Mục tiêu:** delete atomic và outcome rõ ràng.
- **Thao tác:** load managed entity rồi `remove`; missing trả false cho repository.
- **Code/config:** `StudentDAO.delete`.
- **Command:** `mvn -Dtest=StudentServiceIntegrationTest#d01_d02_deleteExistingAndMissingHaveDefinedOutcomes test`.
- **Verification:** existing row biến mất; service đổi false thành not-found.
- **PASS/FAIL:** `[ ]` PASS khi cả D01/D02 đúng.

## 12. Repository contract

- **Mục tiêu:** consumer không phụ thuộc Hibernate/DAO.
- **Thao tác:** đọc `IStudentRepository`.
- **Code/config:** interface chỉ dùng domain types, `List`, `Optional`.
- **Command:** `rg "org.hibernate" src/main/java/fu/edu/sba301/ch18/repository`.
- **Verification:** command không có kết quả.
- **PASS/FAIL:** `[ ]` PASS khi contract không leak Session/Transaction.

## 13. Repository implementation

- **Mục tiêu:** adapter mỏng ủy quyền persistence.
- **Thao tác:** inject `StudentDAO` bằng constructor.
- **Code/config:** `repository/StudentRepository.java`.
- **Command:** `mvn -DskipTests compile`.
- **Verification:** compile và không có validation/HQL trong repository.
- **PASS/FAIL:** `[ ]` PASS khi layer dependency đi một chiều.

## 14. Service contract

- **Mục tiêu:** định nghĩa use cases cho Main/JavaFX.
- **Thao tác:** đọc `IStudentService`.
- **Code/config:** CRUD + email + marks + paging + atomic batch.
- **Command:** `rg "Session|Transaction|StudentDAO" src/main/java/fu/edu/sba301/ch18/service/IStudentService.java`.
- **Verification:** command không có kết quả.
- **PASS/FAIL:** `[ ]` PASS khi presentation chỉ cần service.

## 15. Service validation/business boundary

- **Mục tiêu:** reject null/blank/email sai/marks ngoài 0..10/duplicate/not-found có nghĩa.
- **Thao tác:** chạy negative tests.
- **Code/config:** `StudentService`, package `exception`.
- **Command:** `mvn -Dtest=StudentServiceIntegrationTest#u02_u03_invalidMarksAndMissingUpdateAreRejected test`.
- **Verification:** exception types/messages cụ thể, persistence không bị gọi cho marks sai.
- **PASS/FAIL:** `[ ]` PASS khi lỗi không bị biến thành RuntimeException mơ hồ.

## 16. Composition root / Main

- **Mục tiêu:** wire dependency đúng một chỗ và sẵn sàng thay consumer bằng JavaFX.
- **Thao tác:** Spring tạo SessionFactory → DAO → Repository → Service → runner.
- **Code/config:** `app/Main.java`.
- **Command:** set DB env rồi `mvn spring-boot:run`.
- **Verification:** console có CREATE/READ/SEARCH/LIST/PAGE/DIRTY/MERGE/ROLLBACK/NOTFOUND/DELETE.
- **PASS/FAIL:** `[ ]` PASS khi demo chỉ gọi service.

## 17. CREATE negative — duplicate email

- **Mục tiêu:** cả pre-check và DB UNIQUE bảo vệ dữ liệu.
- **Thao tác:** create hai lần cùng normalized email.
- **Code/config:** `StudentService.create`, unique column.
- **Command:** `mvn -Dtest=StudentServiceIntegrationTest#c02_duplicateEmailIsRejectedAndOnlyOneRowExists test`.
- **Verification:** query duplicate trong `scripts/verification.sql` trả zero rows.
- **PASS/FAIL:** `[ ]` PASS khi chỉ còn một row.

## 18. READ negative — missing ID

- **Mục tiêu:** not-found có nghĩa.
- **Thao tác:** `service.getById(999999L)`.
- **Code/config:** `StudentNotFoundException`.
- **Command:** chạy test R02 ở bước 7.
- **Verification:** message `Student not found: 999999`.
- **PASS/FAIL:** `[ ]` PASS khi không trả null/NPE.

## 19. UPDATE negative

- **Mục tiêu:** invalid data bị chặn trước persistence; missing ID riêng biệt.
- **Thao tác:** marks `-1`, `12`, ID `999999`.
- **Code/config:** `validateMarks`, `updateMarks`.
- **Command:** chạy test U02/U03 ở bước 15.
- **Verification:** hai validation exception và một not-found exception.
- **PASS/FAIL:** `[ ]` PASS khi DB state không đổi.

## 20. DELETE + FK awareness

- **Mục tiêu:** xóa owner và join row nhưng giữ shared Book.
- **Thao tác:** hai Student dùng một Book, xóa một Student.
- **Code/config:** cascade chỉ `PERSIST, MERGE`, không `REMOVE`.
- **Command:** `mvn -Dtest=StudentServiceIntegrationTest#deleteStudentRemovesJoinRowsButDoesNotDeleteSharedBook test`.
- **Verification:** Student=1, Book=1, join rows=1.
- **PASS/FAIL:** `[ ]` PASS khi shared Book không bị xóa.

## 21. Transaction rollback experiment

- **Mục tiêu:** chứng minh không partial commit.
- **Thao tác:** persist hai Student trùng email qua `createBatchAtomically`.
- **Code/config:** `StudentDAO.saveAllAtomically` + `flush()` trong một transaction.
- **Command:** `mvn -Dtest=StudentServiceIntegrationTest#t01_twoWritesAndSecondConstraintFailureRollBackTheWholeTransaction test`.
- **Verification:** count theo email bằng 0.
- **PASS/FAIL:** `[ ]` PASS khi cả INSERT đầu cũng rollback.

## 22. Paging DAO extension

- **Mục tiêu:** phân trang deterministic và input an toàn.
- **Thao tác:** `.setFirstResult(offset).setMaxResults(limit)`.
- **Code/config:** `findPage`, service giới hạn limit `1..100`.
- **Command:** chạy test C01/R03.
- **Verification:** offset 1/limit 1 trả đúng student thứ hai.
- **PASS/FAIL:** `[ ]` PASS khi page đúng và order by ID.

## 23. Break-It — Session singleton

- **Mục tiêu:** nhận biết stale context/resource/thread-safety problem mà không merge anti-pattern.
- **Thao tác:** chỉ thảo luận/thử trên branch tạm: giữ một Session field, đọc entity, sửa DB từ session khác rồi đọc lại.
- **Code/config:** code chính giữ `SessionFactory` singleton nhưng Session short-lived trong try-with-resources.
- **Command:** `rg "Session session" src/main/java`.
- **Verification:** Session chỉ là local variable trong DAO transaction helper.
- **PASS/FAIL:** `[ ]` PASS khi không có Session field.

## 24. Break-It — commit từng DAO step

- **Mục tiêu:** thấy transaction boundary phải ở toàn use case multi-step.
- **Thao tác:** so sánh hai lần `save()` riêng với một `saveAllAtomically()`; bước hai cố ý duplicate.
- **Code/config:** atomic API giữ một Session/transaction cho toàn batch.
- **Command:** chạy T01.
- **Verification:** atomic path count=0; hai commit riêng sẽ để lại row đầu (anti-pattern evidence).
- **PASS/FAIL:** `[ ]` PASS khi use case cần atomicity không gọi hai DAO transaction rời.

## 25. CRUD verification matrix

| ID | Case | Automated evidence | Expected |
|---|---|---|---|
| C01 | Create valid | `c01_r01...` | row + generated ID |
| C02 | Duplicate email | `c02...` | rejected, one row |
| R01 | Existing ID | `c01_r01...` | Student |
| R02 | Missing ID | `r02...` | meaningful not-found |
| R03 | List ordered | `c01_r01...` | IDs ascending |
| U01 | Managed + detached update | `u01...` | row updated |
| U02 | Invalid marks | `u02_u03...` | rejected |
| U03 | Missing update | `u02_u03...` | not-found |
| D01 | Delete existing | `d01_d02...` | row removed |
| D02 | Delete missing | `d01_d02...` | not-found |
| T01 | Second write fails | `t01...` | zero partial rows |
| L01 | Consumer calls service | architecture grep | layering pass |

- **Mục tiêu:** một command thu evidence toàn matrix.
- **Thao tác:** chạy suite và SQL verification trên DB thật.
- **Code/config:** integration test + `scripts/verification.sql`.
- **Command:** `mvn test`.
- **Verification:** `Tests run: 8, Failures: 0, Errors: 0`.
- **PASS/FAIL:** `[ ]` PASS khi test xanh và SQL state khớp.

## 26. Integration Gate G1–G14

| Gate | Evidence cần nộp |
|---|---|
| G1 Build | Maven BUILD SUCCESS |
| G2 Config | Hibernate connects `LibraryDB` |
| G3 Bootstrap | một SessionFactory lifecycle |
| G4 Entity | schema/mapping test passes |
| G5 Create | C01 + INSERT query |
| G6 Read | R01/R02/R03 + search |
| G7 Update | U01 log UPDATE for dirty/merge |
| G8 Delete | D01/D02 + FK test |
| G9 Rollback | T01 count=0 |
| G10 DAO | Hibernate code isolated in DAO/util |
| G11 Repository | contract has no Hibernate types |
| G12 Service | validation/use-case tests |
| G13 Negative | C02/R02/U02/U03/D02 |
| G14 Explain | managed object auto dirty-check; detached object needs merge and returned managed instance |

- **Mục tiêu:** kết luận readiness trước Part B JavaFX.
- **Thao tác:** điền PASS/FAIL và đính kèm test report/SQL screenshots.
- **Code/config:** toàn project.
- **Command:** `mvn clean verify` rồi chạy `scripts/verification.sql` trong SSMS/sqlcmd.
- **Verification:** 14 evidence items ở bảng đều có kết quả.
- **PASS/FAIL:** `[ ]` PASS khi G1–G14 đều đạt; nếu G2 chưa đạt do TCP/credential thì ghi FAIL riêng, không giả lập evidence.

## Module/runtime dependency awareness

Project này cố ý không dùng `module-info.java`: Spring Boot executable jar và Hibernate reflection chạy ổn định trên classpath. Nếu bài tập bắt buộc JPMS, cần `requires org.hibernate.orm.core`, `requires jakarta.persistence`, `requires java.sql` và `opens fu.edu.sba301.ch18.entity to org.hibernate.orm.core`; đây là biến thể nâng cao, không nên trộn vào CRUD baseline trước khi G1–G14 xanh.
