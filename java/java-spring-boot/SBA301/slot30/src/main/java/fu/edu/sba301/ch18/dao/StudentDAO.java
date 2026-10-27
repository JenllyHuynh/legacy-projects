package fu.edu.sba301.ch18.dao;

import fu.edu.sba301.ch18.entity.Student;
import fu.edu.sba301.ch18.exception.DuplicateEmailException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.exception.ConstraintViolationException;

import java.sql.SQLIntegrityConstraintViolationException;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public class StudentDAO {
    private final SessionFactory sessionFactory;

    public StudentDAO(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public Student save(Student student) {
        try {
            return inTransaction(session -> {
                session.persist(student);
                return student;
            });
        } catch (RuntimeException exception) {
            throw translateConstraint(exception, student.getEmail());
        }
    }

    public List<Student> saveAllAtomically(List<Student> students) {
        try {
            return inTransaction(session -> {
                students.forEach(session::persist);
                // Force SQL before commit so a constraint failure is caught by this boundary.
                session.flush();
                return List.copyOf(students);
            });
        } catch (RuntimeException exception) {
            String email = students.isEmpty() ? "<unknown>" : students.get(0).getEmail();
            throw translateConstraint(exception, email);
        }
    }

    public List<Student> findAll() {
        return inTransaction(session -> session.createQuery(
                        "from Student s order by s.id", Student.class)
                .getResultList());
    }

    public Optional<Student> findById(Long id) {
        return inTransaction(session -> Optional.ofNullable(session.get(Student.class, id)));
    }

    public Optional<Student> findByEmail(String email) {
        return inTransaction(session -> session.createQuery(
                        "from Student s where s.email = :email", Student.class)
                .setParameter("email", email)
                .uniqueResultOptional());
    }

    /** Detached update path: always use and return the managed instance from merge(). */
    public Student update(Student detached) {
        try {
            return inTransaction(session -> session.merge(detached));
        } catch (RuntimeException exception) {
            throw translateConstraint(exception, detached.getEmail());
        }
    }

    /** Managed update path: Hibernate dirty checking creates the UPDATE at flush/commit. */
    public Student updateMarks(Long id, double marks) {
        return inTransaction(session -> {
            Student managed = session.get(Student.class, id);
            if (managed == null) {
                throw new IllegalArgumentException("Student not found: " + id);
            }
            managed.setMarks(marks);
            return managed;
        });
    }

    public boolean delete(Long id) {
        return inTransaction(session -> {
            Student managed = session.get(Student.class, id);
            if (managed == null) {
                return false;
            }
            // Student owns STUDENT_BOOKS. Hibernate removes join rows first; Book has no REMOVE cascade.
            session.remove(managed);
            return true;
        });
    }

    public List<Student> findPage(int offset, int limit) {
        return inTransaction(session -> session.createQuery(
                        "from Student s order by s.id", Student.class)
                .setFirstResult(offset)
                .setMaxResults(limit)
                .getResultList());
    }

    private <T> T inTransaction(Function<Session, T> work) {
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            try {
                T result = work.apply(session);
                transaction.commit();
                return result;
            } catch (RuntimeException exception) {
                if (transaction.isActive()) {
                    transaction.rollback();
                }
                throw exception;
            }
        }
    }

    private RuntimeException translateConstraint(RuntimeException exception, String email) {
        Throwable current = exception;
        while (current != null) {
            if (current instanceof ConstraintViolationException
                    || current instanceof SQLIntegrityConstraintViolationException) {
                return new DuplicateEmailException(email);
            }
            current = current.getCause();
        }
        return exception;
    }
}
