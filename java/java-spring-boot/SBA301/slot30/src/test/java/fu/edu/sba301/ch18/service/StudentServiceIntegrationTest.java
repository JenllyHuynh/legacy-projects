package fu.edu.sba301.ch18.service;

import fu.edu.sba301.ch18.dao.StudentDAO;
import fu.edu.sba301.ch18.entity.Book;
import fu.edu.sba301.ch18.entity.Student;
import fu.edu.sba301.ch18.exception.DuplicateEmailException;
import fu.edu.sba301.ch18.exception.StudentNotFoundException;
import fu.edu.sba301.ch18.exception.StudentValidationException;
import fu.edu.sba301.ch18.repository.StudentRepository;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class StudentServiceIntegrationTest {
    private static SessionFactory sessionFactory;
    private IStudentService service;

    @BeforeAll
    static void buildSessionFactory() {
        sessionFactory = new Configuration()
                .configure("hibernate-test.cfg.xml")
                .buildSessionFactory();
    }

    @AfterAll
    static void closeSessionFactory() {
        sessionFactory.close();
    }

    @BeforeEach
    void setUp() {
        clearDatabase();
        StudentDAO dao = new StudentDAO(sessionFactory);
        service = new StudentService(new StudentRepository(dao));
    }

    @Test
    void c01_r01_r03_createReadAndListInDeterministicOrder() {
        Student first = service.create(student("first@fu.edu.vn", 8));
        Student second = service.create(student("second@fu.edu.vn", 9));

        assertThat(first.getId()).isPositive();
        assertThat(service.getById(first.getId()).getEmail()).isEqualTo("first@fu.edu.vn");
        assertThat(service.getByEmail(" FIRST@FU.EDU.VN ").getId()).isEqualTo(first.getId());
        assertThat(service.getAll()).extracting(Student::getId)
                .containsExactly(first.getId(), second.getId());
        assertThat(service.getPage(1, 1)).extracting(Student::getId)
                .containsExactly(second.getId());
    }

    @Test
    void c02_duplicateEmailIsRejectedAndOnlyOneRowExists() {
        service.create(student("duplicate@fu.edu.vn", 8));

        assertThatThrownBy(() -> service.create(student("DUPLICATE@fu.edu.vn", 9)))
                .isInstanceOf(DuplicateEmailException.class)
                .hasMessageContaining("duplicate@fu.edu.vn");
        assertThat(count("select count(s) from Student s where s.email = :email", "duplicate@fu.edu.vn"))
                .isOne();
    }

    @Test
    void r02_missingIdHasMeaningfulException() {
        assertThatThrownBy(() -> service.getById(999_999L))
                .isInstanceOf(StudentNotFoundException.class)
                .hasMessage("Student not found: 999999");
    }

    @Test
    void u01_managedDirtyCheckingAndDetachedMergeBothUpdateTheRow() {
        Student created = service.create(student("update@fu.edu.vn", 7));

        Student dirtyChecked = service.updateMarks(created.getId(), 9.5);
        dirtyChecked.setFirstName("Merged");
        Student mergeResult = service.update(dirtyChecked);

        assertThat(mergeResult).isNotSameAs(dirtyChecked);
        Student reloaded = service.getById(created.getId());
        assertThat(reloaded.getMarks()).isEqualTo(9.5);
        assertThat(reloaded.getFirstName()).isEqualTo("Merged");
    }

    @Test
    void u02_u03_invalidMarksAndMissingUpdateAreRejected() {
        assertThatThrownBy(() -> service.updateMarks(1L, -1))
                .isInstanceOf(StudentValidationException.class);
        assertThatThrownBy(() -> service.updateMarks(1L, 12))
                .isInstanceOf(StudentValidationException.class);
        assertThatThrownBy(() -> service.updateMarks(999_999L, 8))
                .isInstanceOf(StudentNotFoundException.class);
    }

    @Test
    void d01_d02_deleteExistingAndMissingHaveDefinedOutcomes() {
        Student created = service.create(student("delete@fu.edu.vn", 8));
        service.delete(created.getId());

        assertThatThrownBy(() -> service.getById(created.getId()))
                .isInstanceOf(StudentNotFoundException.class);
        assertThatThrownBy(() -> service.delete(created.getId()))
                .isInstanceOf(StudentNotFoundException.class);
    }

    @Test
    void t01_twoWritesAndSecondConstraintFailureRollBackTheWholeTransaction() {
        List<Student> batch = List.of(
                student("atomic@fu.edu.vn", 8),
                student("atomic@fu.edu.vn", 9));

        assertThatThrownBy(() -> service.createBatchAtomically(batch))
                .isInstanceOf(DuplicateEmailException.class);
        assertThat(count("select count(s) from Student s where s.email = :email", "atomic@fu.edu.vn"))
                .isZero();
    }

    @Test
    void deleteStudentRemovesJoinRowsButDoesNotDeleteSharedBook() {
        Long[] ids = persistTwoStudentsSharingOneBook();

        service.delete(ids[0]);

        assertThat(count("select count(b) from Book b", null)).isOne();
        assertThat(count("select count(s) from Student s", null)).isOne();
        assertThat(nativeCount("select count(*) from STUDENT_BOOKS")).isOne();
    }

    private Student student(String email, double marks) {
        return new Student(email, "First", "Last", marks);
    }

    private Long count(String hql, String email) {
        try (Session session = sessionFactory.openSession()) {
            var query = session.createQuery(hql, Long.class);
            if (email != null) {
                query.setParameter("email", email);
            }
            return query.getSingleResult();
        }
    }

    private long nativeCount(String sql) {
        try (Session session = sessionFactory.openSession()) {
            return session.createNativeQuery(sql, Long.class).getSingleResult();
        }
    }

    private Long[] persistTwoStudentsSharingOneBook() {
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            Book shared = new Book("9780134685991", "Effective Java");
            Student first = student("owner1@fu.edu.vn", 8);
            Student second = student("owner2@fu.edu.vn", 9);
            first.addBook(shared);
            second.addBook(shared);
            session.persist(first);
            session.persist(second);
            transaction.commit();
            return new Long[]{first.getId(), second.getId()};
        }
    }

    private void clearDatabase() {
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            session.createNativeMutationQuery("delete from STUDENT_BOOKS").executeUpdate();
            session.createMutationQuery("delete from Student").executeUpdate();
            session.createMutationQuery("delete from Book").executeUpdate();
            transaction.commit();
        }
    }
}
