# SBA301 — Slot 30 — Chapter 18 Part A

## Hibernate CRUD Application • Layers • Configuration

Dự án thực hành xây dựng CRUD bằng **Spring Boot + native Hibernate + SQL Server**. Trọng tâm là chuỗi trách nhiệm, transaction, validation và evidence — không chỉ bốn thao tác CRUD.

```text
Main / future JavaFX controller
  → IStudentService / StudentService       (use case, validation)
  → IStudentRepository / StudentRepository (persistence contract)
  → StudentDAO                             (Session, HQL, transaction)
  → SessionFactory → JDBC → SQL Server     (database state/evidence)
```

Mỗi bước tuân theo: **Mục tiêu → thao tác → code/config → command → verification → PASS/FAIL**.

## 1. Mục tiêu học tập

Sinh viên sẽ biết tổ chức layer; quản lý `SessionFactory`/`Session`; CRUD có transaction/rollback; phân biệt dirty checking và `merge()`; dùng typed HQL/`Optional`; xử lý validation, duplicate, not-found và FK; kiểm chứng bằng test + SQL; chuẩn bị service boundary cho JavaFX Part B.

## 2. Yêu cầu và công nghệ

Kiến thức trước: Java interface/exception/collection/Optional; Maven lifecycle; SQL Server PK/IDENTITY/UNIQUE/FK; Hibernate entity lifecycle, HQL và transaction.

| Thành phần | Phiên bản/vai trò |
|---|---|
| Java / Maven | Java 21 LTS / Maven 3.9.9 |
| Spring Boot | 3.5.16 — bootstrap, DI, external config |
| Hibernate ORM | 6.6.x — native Session API |
| SQL Server + JDBC | Runtime `LibraryDB` |
| H2 MSSQL mode | Integration test không cần credential |
| JUnit 5 + AssertJ | Automated verification |

Project cố ý không dùng Spring Data JPA để sinh viên trực tiếp quan sát Session, HQL và transaction.

## 3. Cấu trúc

```text
slot30/
├─ pom.xml, .env.example, README.md
├─ docs/WORKBOOK.md, docs/KNOWLEDGE_BASE.md
├─ scripts/create-database.sql, scripts/verification.sql
└─ src/
   ├─ main/java/fu/edu/sba301/ch18/
   │  ├─ app/Main.java
   │  ├─ entity/Student.java, Book.java
   │  ├─ dao/StudentDAO.java
   │  ├─ repository/IStudentRepository.java, StudentRepository.java
   │  ├─ service/IStudentService.java, StudentService.java
   │  ├─ exception/*.java
   │  └─ util/HibernateUtil.java
   ├─ main/resources/application.properties, hibernate.cfg.xml
   ├─ test/java/.../StudentServiceIntegrationTest.java
   └─ test/resources/hibernate-test.cfg.xml
```

`entity` giữ mapping; `dao` giữ Hibernate API; `repository` giữ contract; `service` giữ validation/use case; `app` chỉ composition và gọi service.

# 4. Hướng dẫn tái lập từng bước

Chạy trong PowerShell tại project root. Chỉ chuyển bước khi tiêu chí PASS đạt.

## Step 0 — Kiểm tra môi trường

- **Mục tiêu:** Java/Maven đúng phiên bản.
- **Thao tác/command:** `java -version`; `mvn -version`.
- **Verification:** Java 21, Maven ≥ 3.6.3, Maven dùng đúng Java home.
- **PASS/FAIL:** PASS khi `mvn -q -DskipTests compile` exit code 0.

## Step 1 — Khởi tạo Maven project

- **Mục tiêu:** có Boot composition root nhưng persistence là native Hibernate.
- **Thao tác:** tạo [pom.xml](pom.xml) với `spring-boot-starter`, `hibernate-core`, `mssql-jdbc`; test scope gồm H2 và starter-test.
- **Command:** `mvn dependency:tree`.
- **Verification:** đủ Hibernate, Microsoft JDBC, H2, JUnit; không unresolved dependency.
- **PASS/FAIL:** PASS khi Maven đọc model và compile.

## Step 2 — Tạo layer structure

- **Mục tiêu:** persistence không nằm trong Main/UI.
- **Thao tác:** tạo package như mục 3.
- **Command:** `rg --files src/main src/test`.
- **Verification:** `rg -n "createQuery|Session|Transaction" src/main/java/fu/edu/sba301/ch18/service` không trả kết quả.
- **PASS/FAIL:** PASS khi HQL/Hibernate API không nằm trong service/controller.

## Step 3 — Chuẩn bị SQL Server

- **Mục tiêu:** JDBC truy cập được `LibraryDB`.
- **Thao tác:** enable TCP/IP trong SQL Server Configuration Manager; đặt/ghi lại port; restart service; chạy [create-database.sql](scripts/create-database.sql); tạo user thực hành có quyền trên DB.
- **Command:** `Get-NetTCPConnection -LocalPort 1433 -State Listen`.
- **Verification:** `SELECT DB_ID(N'LibraryDB');` khác `NULL`.
- **PASS/FAIL:** PASS khi port listen và đăng nhập DB được. Máy lúc tạo project có SQL Server 2022 service nhưng chưa listen 1433; không được giả lập G2.

## Step 4 — Externalize credential

- **Mục tiêu:** không hard-code/commit secret.
- **Thao tác:** dùng [.env.example](.env.example) làm mẫu và nạp vào shell/IDE:

```powershell
$env:DB_URL='jdbc:sqlserver://localhost:1433;databaseName=LibraryDB;encrypt=true;trustServerCertificate=true'
$env:DB_USER='slot30_user'
$env:DB_PASSWORD='your-local-secret'
$env:APP_DEMO_ENABLED='true'
```

- **Verification:** `.env` được `.gitignore`; `.env.example` không có secret thật. Project không tự đọc `.env`.
- **PASS/FAIL:** PASS khi process nhìn thấy biến và Git không chứa password.

## Step 5 — Hibernate configuration

- **Mục tiêu:** khai báo driver, URL, dialect, schema mode và mappings.
- **Thao tác:** tạo [hibernate.cfg.xml](src/main/resources/hibernate.cfg.xml); `Main` override `${DB_*}` trước `buildSessionFactory()`.
- **Command:** `$env:APP_DEMO_ENABLED='false'; mvn spring-boot:run`.
- **Verification:** log có Database info/SQL Server dialect, không in password.
- **PASS/FAIL:** PASS khi SessionFactory build và schema update được.

## Step 6 — Entity mapping

- **Mục tiêu:** map `STUDENTS`, `BOOKS`, `STUDENT_BOOKS`.
- **Thao tác:** `Student.id` dùng IDENTITY; email NOT NULL/UNIQUE; names NOT NULL; Student là owning side many-to-many; cascade `PERSIST, MERGE`, không `REMOVE`; entity có no-arg constructor.
- **Code:** [Student.java](src/main/java/fu/edu/sba301/ch18/entity/Student.java), [Book.java](src/main/java/fu/edu/sba301/ch18/entity/Book.java).
- **Command:** `mvn -Dtest=StudentServiceIntegrationTest test`.
- **Verification:** Hibernate metadata tạo đủ ba bảng, không `AnnotationException`.
- **PASS/FAIL:** PASS khi mapping bootstrap được.

## Step 7 — SessionFactory lifecycle

- **Mục tiêu:** factory build một lần và close khi ứng dụng kết thúc.
- **Thao tác:** `HibernateUtil.initialize()` build singleton; Spring bean có `destroyMethod="close"`.
- **Command:** `rg -n "buildSessionFactory" src/main/java`.
- **Verification:** chỉ [HibernateUtil.java](src/main/java/fu/edu/sba301/ch18/util/HibernateUtil.java) build factory.
- **PASS/FAIL:** PASS khi DAO/service không tạo factory.

## Step 8 — Transaction helper + CREATE

- **Mục tiêu:** session ngắn hạn, commit/rollback đúng.
- **Thao tác:** trong `try (Session ...)`, begin transaction; work; commit; exception thì rollback. `save()` gọi `persist()`.
- **Code:** [StudentDAO.java](src/main/java/fu/edu/sba301/ch18/dao/StudentDAO.java).
- **Command:** `mvn -Dtest=StudentServiceIntegrationTest#c01_r01_r03_createReadAndListInDeterministicOrder test`.
- **Verification:** `SELECT TOP (20) * FROM dbo.STUDENTS ORDER BY id DESC;` cho generated ID + row.
- **PASS/FAIL:** PASS khi object có ID và DB có row.

## Step 9 — READ/search/paging

- **Mục tiêu:** API/query phù hợp và an toàn.
- **Thao tác:** PK dùng `get()` + `Optional`; list/search dùng typed HQL; email bind `:email`; page dùng first/max result; mọi list có `order by id`.
- **Command:** chạy test ở Step 8.
- **Verification:** IDs tăng dần; email được normalize; offset 1/limit 1 trả phần tử thứ hai.
- **PASS/FAIL:** PASS khi missing DAO trả empty, không nối input vào HQL.

## Step 10 — UPDATE managed/detached

- **Mục tiêu:** hiểu dirty checking và merge.
- **Thao tác:** managed path load + set field + commit, không gọi update; detached path gọi `Student managed = session.merge(detached)` và trả `managed`.
- **Command:** `mvn -Dtest=StudentServiceIntegrationTest#u01_managedDirtyCheckingAndDetachedMergeBothUpdateTheRow test`.
- **Verification:** reload thấy marks/name mới; merge result khác detached reference.
- **PASS/FAIL:** PASS khi cả hai path cập nhật DB đúng.

## Step 11 — DELETE/FK

- **Mục tiêu:** xóa Student/join row nhưng giữ shared Book.
- **Thao tác:** load managed Student rồi `remove()`; không cascade REMOVE many-to-many.
- **Command:** `mvn -Dtest=StudentServiceIntegrationTest#deleteStudentRemovesJoinRowsButDoesNotDeleteSharedBook test`.
- **Verification:** sau xóa một trong hai Student: Student=1, Book=1, join row=1.
- **PASS/FAIL:** PASS khi shared Book còn tồn tại.

## Step 12 — Repository

- **Mục tiêu:** service phụ thuộc persistence abstraction.
- **Thao tác:** interface chỉ dùng domain types/`List`/`Optional`; implementation inject DAO và delegate.
- **Code:** [IStudentRepository.java](src/main/java/fu/edu/sba301/ch18/repository/IStudentRepository.java).
- **Verification:** `rg -n "org.hibernate|Session|Transaction" src/main/java/fu/edu/sba301/ch18/repository` không có kết quả.
- **PASS/FAIL:** PASS khi contract không leak Hibernate.

## Step 13 — Service validation/exceptions

- **Mục tiêu:** invalid, duplicate, missing trở thành error có nghĩa.
- **Thao tác:** trim/lowercase email; kiểm format/length; names bắt buộc; marks finite `0..10`; ID dương; dùng `StudentValidationException`, `DuplicateEmailException`, `StudentNotFoundException`.
- **Code:** [StudentService.java](src/main/java/fu/edu/sba301/ch18/service/StudentService.java).
- **Command:** `mvn -Dtest=StudentServiceIntegrationTest#u02_u03_invalidMarksAndMissingUpdateAreRejected test`.
- **Verification:** `-1`, `12` → validation; missing ID → not-found, không NPE.
- **PASS/FAIL:** PASS khi lỗi rõ và DB không đổi.

## Step 14 — Composition root/Main

- **Mục tiêu:** wire đúng một nơi; consumer chỉ gọi service.
- **Thao tác:** [Main.java](src/main/java/fu/edu/sba301/ch18/app/Main.java) tạo `SessionFactory → DAO → Repository → Service → ApplicationRunner`.
- **Command:** nạp DB env rồi `mvn spring-boot:run`.
- **Verification:** console có CREATE/READ/SEARCH/LIST/PAGE/DIRTY/MERGE/ROLLBACK/NOTFOUND/DELETE.
- **PASS/FAIL:** PASS khi demo chạy và factory đóng sạch.

## Step 15 — Duplicate/atomic rollback

- **Mục tiêu:** write thứ hai fail không để partial row.
- **Thao tác:** `createBatchAtomically()` persist hai Student trùng email trong một transaction; `flush()` kích hoạt constraint trước commit.
- **Command:** `mvn -Dtest=StudentServiceIntegrationTest#t01_twoWritesAndSecondConstraintFailureRollBackTheWholeTransaction test`.
- **Verification:** count theo email bằng 0. SQL error 23505 là intentional evidence, không phải test failure.
- **PASS/FAIL:** PASS khi rollback cả INSERT đầu.

## Step 16 — Full gate và đóng gói

- **Mục tiêu:** regression + executable artifact.
- **Command:** `mvn clean verify`.
- **Verification:** `Tests run: 8, Failures: 0, Errors: 0`; `BUILD SUCCESS`; có `target/chapter18-hibernate-crud-1.0.0.jar`.
- **PASS/FAIL:** PASS khi cả ba điều kiện đúng.

## Step 17 — SQL evidence thật

- **Mục tiêu:** đối chiếu application output với DB state.
- **Thao tác:** chạy [verification.sql](scripts/verification.sql).
- **Verification:** lưu evidence generated ID, no duplicate, updated row, deleted row, Book/join rows, rollback count=0 và unique indexes.
- **PASS/FAIL:** PASS khi automated result và SQL state thống nhất.

## Step 18 — Architecture audit

- **Mục tiêu:** loại Session singleton, HQL trong service và consumer gọi DAO.
- **Commands:**

```powershell
rg -n "from Student|createQuery" src/main/java -g '!**/dao/**'
rg -n "private .*Session(?!Factory)" src/main/java --pcre2
rg -n "dao\.(save|find|update|delete)" src/main/java/fu/edu/sba301/ch18/app
```

- **PASS/FAIL:** PASS khi cả ba không trả anti-pattern.

## 5. CRUD verification matrix

| ID | Case | Expected |
|---|---|---|
| C01/C02 | Create valid/duplicate | Generated row / rejected, no duplicate |
| R01/R02/R03 | Existing/missing/list | Student / not-found / ordered list |
| U01/U02/U03 | Valid/invalid/missing update | Updated / rejected / not-found |
| D01/D02 | Existing/missing delete | Removed / defined not-found |
| T01 | Two writes, second fails | Whole transaction rollback |
| FK01 | Delete Student with shared Book | Join removed; Book retained |
| L01 | Main/UI call | Service only |

## 6. Troubleshooting

- **Connection refused:** SQL Server service chạy chưa đủ; enable TCP/IP, kiểm port và restart.
- **`DB_USER is required`:** `.env.example` không tự load; nạp biến vào đúng process.
- **Login failed:** kiểm authentication mode, login mapping/quyền; không hard-code password để “sửa”.
- **Unique error nhưng test PASS:** rollback test cố ý tạo lỗi; xem Maven summary và DB count.
- **LazyInitializationException:** không đọc lazy association sau Session close; fetch/map DTO trong transaction, không giữ Session theo UI lifetime.
- **Detached entity passed to persist:** entity cũ cần `merge()` và dùng reference được trả về.

## 7. Tài liệu tiếp theo

- [Knowledge Base: 20 trọng tâm cốt lõi](docs/KNOWLEDGE_BASE.md)
- [Workbook 26 bước và Integration Gate G1–G14](docs/WORKBOOK.md)
- [Integration tests](src/test/java/fu/edu/sba301/ch18/service/StudentServiceIntegrationTest.java)
- [SQL evidence](scripts/verification.sql)

Baseline không dùng `module-info.java` vì Spring Boot executable jar/Hibernate reflection chạy trên classpath. Khi học JPMS, cần `requires` phù hợp và `opens` entity package cho Hibernate; không trộn biến thể đó trước khi CRUD gates đã xanh.
