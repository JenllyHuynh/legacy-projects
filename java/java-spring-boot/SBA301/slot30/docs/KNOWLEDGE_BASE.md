# Knowledge Base — Hibernate CRUD, Layers & Configuration

Tài liệu này giải thích “vì sao” phía sau project Slot 30. Mỗi mục gồm khái niệm, minh họa trong project, cách kiểm chứng và lỗi thường gặp.

## Mô hình tinh thần tổng quát

```text
Request/use case
  → Service kiểm input và business rule
  → Repository biểu diễn nhu cầu persistence
  → DAO mở Session + Transaction
  → Hibernate chuyển entity operation thành SQL
  → DB kiểm PK/UNIQUE/FK
  → commit hoặc rollback
  → kết quả/error có nghĩa đi ngược lên consumer
```

Entity thường đi qua các trạng thái:

```text
new Student(...) --persist()--> managed --close Session--> detached
       transient                 │ dirty checking              │
                                 └--remove()--> removed        └--merge()--> managed copy
```

## 2.1. Configuration is runtime infrastructure

Configuration quyết định ứng dụng **thực sự nói chuyện với DB nào**, dùng driver/dialect nào, map entity nào và xử lý schema ra sao. Đây là runtime infrastructure, không phải business rule.

Trong `hibernate.cfg.xml`:

```xml
<property name="hibernate.connection.driver_class">com.microsoft.sqlserver.jdbc.SQLServerDriver</property>
<property name="hibernate.dialect">org.hibernate.dialect.SQLServerDialect</property>
<property name="hibernate.hbm2ddl.auto">update</property>
<mapping class="fu.edu.sba301.ch18.entity.Student"/>
```

Dialect giúp Hibernate sinh SQL phù hợp SQL Server. `hbm2ddl.auto=update` tiện cho lab nhưng production thường dùng migration tool và `validate`. Mapping bị thiếu sẽ gây “Unknown entity” dù Java class vẫn compile.

**Kiểm chứng:** chạy app và đọc `Database info`; query `SELECT DB_NAME()`. **Lỗi thường gặp:** URL đúng cú pháp nhưng sai instance/port; service SQL chạy nhưng TCP chưa enable.

## 2.2. SessionFactory lifecycle

`SessionFactory` chứa metadata mapping và service Hibernate; tạo nó tốn chi phí nhưng nó thread-safe. Quy tắc: **một factory cho một application/configuration**, build lúc bootstrap và close lúc shutdown.

Project build ở `HibernateUtil.initialize()` và Spring quản lý close. Không được gọi `new Configuration().buildSessionFactory()` trong mỗi CRUD.

**Kiểm chứng:** `rg -n "buildSessionFactory" src/main/java` chỉ ra một vị trí. **Anti-pattern:** tạo factory cho mỗi click UI làm chậm, tăng connection/resource usage và chia cắt persistence context.

## 2.3. Session lifecycle

`Session` là persistence context/unit of work, **không thread-safe** và giữ first-level cache. Nó nên ngắn hạn:

```java
try (Session session = sessionFactory.openSession()) {
    // one unit of work
}
```

Không giữ Session trong singleton, controller hay toàn bộ JavaFX lifetime. Session dài hạn dễ stale data, memory growth, lazy-loading bất ngờ và cross-thread corruption.

**Minh họa:** `StudentDAO.inTransaction()` mở/đóng Session cục bộ. **Thí nghiệm break-it:** Session A load row; Session B update/commit; Session A `get()` lại có thể trả object cache cũ nếu không refresh/clear.

## 2.4. Transaction boundary

Transaction xác định nhóm thay đổi **hoặc cùng thành công, hoặc cùng thất bại**.

```java
Transaction tx = session.beginTransaction();
try {
    T result = work.apply(session);
    tx.commit();
    return result;
} catch (RuntimeException ex) {
    if (tx.isActive()) tx.rollback();
    throw ex;
}
```

Một use case hai write không nên gọi hai DAO method tự commit độc lập nếu cần atomicity. Project dùng `saveAllAtomically()` để giữ cả hai persist trong một transaction.

**Kiểm chứng:** test T01 cố ý vi phạm UNIQUE ở write thứ hai; count cuối phải bằng 0. Nếu bằng 1, boundary đã bị phân mảnh.

## 2.5. CREATE

Object vừa `new` là transient. `persist()` gắn nó vào persistence context thành managed:

```java
Student s = new Student(...); // transient
session.persist(s);           // managed
```

Với `GenerationType.IDENTITY`, INSERT thường xảy ra sớm để DB trả generated ID. Vì vậy thời điểm có ID phụ thuộc strategy; không nên tự đoán/gán ID.

Service pre-check duplicate giúp message thân thiện, nhưng DB UNIQUE mới là bảo vệ cuối trước concurrent requests.

**Kiểm chứng:** sau create, `id != null` và SQL query thấy row. **Lỗi thường gặp:** dùng `persist()` cho detached entity có ID cũ.

## 2.6. READ

Chọn API theo ý nghĩa:

- `session.get(Student.class, id)`: lookup PK, trả entity hoặc `null`.
- HQL: semantic query trên entity/property, không phải table/column.
- Typed query: `createQuery(hql, Student.class)` loại bỏ cast risk.
- Named parameter: chống lỗi nối chuỗi và injection.

```java
session.createQuery("from Student s where s.email = :email", Student.class)
       .setParameter("email", email)
       .uniqueResultOptional();
```

List/paging phải có `order by`; nếu không, DB không bảo đảm thứ tự. **Kiểm chứng:** test R01/R02/R03 và paging.

## 2.7. UPDATE — managed path

Entity load trong Session hiện tại là managed. Hibernate chụp state và so sánh lúc flush/commit (dirty checking):

```java
Student managed = session.get(Student.class, id);
managed.setMarks(9.0);
tx.commit(); // Hibernate phát hiện thay đổi và sinh UPDATE
```

Không cần gọi `session.update()` cho path này. Validation phải diễn ra trước khi set/commit.

**Kiểm chứng:** bật SQL log, thấy UPDATE tại flush/commit; reload ở Session mới thấy marks mới.

## 2.8. UPDATE — detached path

Sau khi Session đóng, object còn tồn tại nhưng là detached. `merge(detached)` **copy state** sang một managed instance và trả instance đó:

```java
Student managed = session.merge(detached);
return managed;
```

Object truyền vào không tự biến thành managed. Bỏ qua return value của `merge()` là lỗi tư duy phổ biến.

**Kiểm chứng:** test U01 xác nhận `mergeResult` không cùng reference với detached nhưng DB có state mới. **Nguy cơ:** merge một graph thiếu/stale field có thể ghi đè dữ liệu; DTO/patch use case thường an toàn hơn cho ứng dụng lớn.

## 2.9. DELETE

Đường đi dễ hiểu nhất là load managed entity rồi remove:

```java
Student managed = session.get(Student.class, id);
if (managed != null) session.remove(managed);
```

Phải xem FK, owning side và cascade. Với many-to-many, cascade REMOVE có thể xóa Book dùng chung — thường là sai. Project chỉ cascade PERSIST/MERGE; Hibernate xóa join row trước Student và giữ Book.

**Kiểm chứng:** test FK01: sau delete còn một Student, một Book, một join row.

## 2.10. Not-found semantics

DAO có thể biểu diễn “không có row” bằng `Optional.empty()` hoặc boolean; đó chưa nhất thiết là application error. Service quyết định semantics:

```java
return repository.findById(id)
    .orElseThrow(() -> new StudentNotFoundException(id));
```

Điều này tránh `null` lan lên UI và tránh NPE không có ngữ cảnh. Với list query, empty list thường tự nhiên hơn Optional list.

## 2.11. Validation boundary

Ba lớp bảo vệ bổ sung nhau:

1. Service validation: message nhanh, đúng use case (`marks 0..10`).
2. Entity/Bean annotations: mô tả object contract nếu có validation engine.
3. DB constraints: integrity cuối cùng trước concurrency/client khác.

`@Column(nullable=false)` không thay thế service validation: lỗi DB đến muộn và technical hơn. Ngược lại, pre-check duplicate không thay thế UNIQUE vì có race condition.

**Minh họa:** `StudentService` trim/lowercase email, kiểm format/name/marks/ID; DB giữ UNIQUE email.

## 2.12. DAO

DAO chứa chi tiết Hibernate-specific:

- mở Session/Transaction;
- `persist/get/merge/remove`;
- typed HQL, paging;
- rollback và dịch constraint persistence thành lỗi có nghĩa.

DAO không biết JavaFX/controller. Trong project, mọi `createQuery` nằm ở `StudentDAO`. Điều này làm persistence dễ thay đổi/test và giữ upper layer sạch.

## 2.13. Repository

Repository mô tả collection/persistence theo ngôn ngữ application:

```java
Optional<Student> findByEmail(String email);
Student updateMarks(Long id, double marks);
```

Interface không leak `Session`, `Transaction` hay Hibernate query. `StudentRepository` hiện là adapter mỏng qua DAO; dù nhìn có vẻ “thừa”, nó tạo seam để mock/test hoặc thay persistence implementation.

## 2.14. Service

Service là use-case boundary:

- normalize/validate input;
- orchestration qua repository;
- duplicate/not-found semantics;
- quyết định transaction ownership cho use case nhiều bước.

Service không chứa HQL. Main/JavaFX chỉ phụ thuộc `IStudentService`. Khi use case gồm nhiều write atomic, boundary phải được thiết kế để repository/DAO thực thi trong cùng transaction, như `createBatchAtomically()`.

## 2.15. Configuration separation

Không đặt password trong Java, XML committed, README hay test log. Project giữ placeholder và override bằng environment:

```powershell
$env:DB_USER='slot30_user'
$env:DB_PASSWORD='local-secret'
```

`.env.example` chỉ chứa tên biến/mẫu; `.env` thật bị ignore. Production nên dùng secret manager và account least privilege. URL cũng externalize vì dev/test/prod khác host/database.

## 2.16. Exception strategy

Hai việc khác nhau phải cùng xảy ra:

1. DAO rollback technical transaction.
2. Upper layer nhận error có nghĩa.

Project có `StudentValidationException`, `StudentNotFoundException`, `DuplicateEmailException`. DAO duyệt cause chain của constraint exception và dịch duplicate; các lỗi không nhận diện không bị nuốt.

Không dùng `catch (Exception) { throw new RuntimeException("Error"); }`: cách này mất SQL state, cause và ý nghĩa. Không log rồi tiếp tục như thành công sau rollback.

## 2.17. Logging

`show_sql`, `format_sql`, `highlight_sql` hữu ích trong lab để nối operation với SQL. Quan sát:

- INSERT lúc persist/IDENTITY;
- UPDATE lúc dirty-check flush;
- DELETE join row trước owner;
- constraint error và rollback.

Không log password, token, connection secret hay dữ liệu cá nhân. Production dùng structured logger, parameter redaction và level phù hợp; không để verbose SQL vô điều kiện.

## 2.18. CRUD test discipline

Mỗi happy path cần counterpart:

| Happy path | Negative/constraint counterpart |
|---|---|
| Create valid | duplicate email |
| Get existing | missing ID |
| Update valid | invalid marks, missing ID |
| Delete existing | delete missing, FK/shared Book |
| Two writes | second fails → atomic rollback |

Test phải kiểm **DB state**, không chỉ “không throw”. Test suite dùng H2 MSSQL mode để chạy nhanh; SQL Server thật vẫn cần integration gate riêng vì dialect/driver/schema behavior có thể khác.

## 2.19. Idempotency awareness

Idempotent nghĩa là lặp cùng request không làm state tiếp tục thay đổi ngoài kết quả lần đầu.

- READ là idempotent.
- Set marks về một giá trị cụ thể thường idempotent; “increase marks by 1” không.
- DELETE ở API thường được thiết kế idempotent (lần sau vẫn coi resource absent), nhưng DAO có thể trả `false` và service hiện phát not-found.
- CREATE thông thường không idempotent; retry có thể duplicate. UNIQUE email giảm duplicate theo business key nhưng không thay thế idempotency key cho API thực tế.

Quan trọng là contract phải ghi rõ, không tranh luận “DELETE luôn luôn phải trả gì” mà không xét layer/use case.

## 2.20. Layer dependency

Dependency đúng:

```text
presentation → service interface → repository interface → implementation/DAO → Hibernate/DB
```

Persistence không import UI. Service không import Hibernate. Entity không biết Main. Composition root là nơi duy nhất biết concrete implementations.

**Architecture verification:**

```powershell
rg -n "from Student|createQuery" src/main/java -g '!**/dao/**'
rg -n "org.hibernate" src/main/java/fu/edu/sba301/ch18/service
rg -n "private .*Session(?!Factory)" src/main/java --pcre2
```

Không có kết quả là evidence cho HQL isolation, service independence và absence of Session singleton.

# Trace hoàn chỉnh một request

Ví dụ `service.updateMarks(10L, 9.5)`:

1. Consumer gọi service, không gọi DAO.
2. Service kiểm ID dương và marks trong `0..10`.
3. Repository kiểm entity tồn tại; missing được đổi thành not-found.
4. DAO mở Session và transaction.
5. `session.get()` chạy SELECT; entity trở thành managed.
6. Setter đổi state trong memory.
7. Commit kích hoạt dirty checking và UPDATE.
8. DB kiểm constraint rồi commit.
9. Session đóng; object trả lên trở thành detached.
10. Test/SQL reload xác nhận state, tạo evidence.

Nếu exception xảy ra ở bước 5–8, DAO rollback; error có nghĩa đi lên; không được báo thành công hoặc để partial state.

# Câu hỏi tự kiểm tra

1. Tại sao SessionFactory singleton hợp lý nhưng Session singleton nguy hiểm?
2. Tại sao duplicate pre-check vẫn cần UNIQUE constraint?
3. Vì sao object truyền vào `merge()` không phải object nên tiếp tục sử dụng?
4. Khi nào transaction phải nâng từ một DAO operation lên toàn service use case?
5. Vì sao `CascadeType.REMOVE` nguy hiểm với shared many-to-many?
6. `Optional<Student>` tốt hơn `Student=null` ở DAO boundary như thế nào?
7. Test rollback phải kiểm database state gì?
8. Nếu JavaFX controller chứa HQL, layer dependency đã bị phá ra sao?

Trả lời được tám câu hỏi bằng code và evidence của project là dấu hiệu đã nắm phần cốt lõi Slot 30.
