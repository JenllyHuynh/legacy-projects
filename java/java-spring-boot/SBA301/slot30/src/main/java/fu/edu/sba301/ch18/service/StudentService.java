package fu.edu.sba301.ch18.service;

import fu.edu.sba301.ch18.entity.Student;
import fu.edu.sba301.ch18.exception.DuplicateEmailException;
import fu.edu.sba301.ch18.exception.StudentNotFoundException;
import fu.edu.sba301.ch18.exception.StudentValidationException;
import fu.edu.sba301.ch18.repository.IStudentRepository;
import java.util.List;
import java.util.Locale;

public class StudentService implements IStudentService {
    private final IStudentRepository repository;

    public StudentService(IStudentRepository repository) {
        this.repository = repository;
    }

    @Override
    public Student create(Student student) {
        normalizeAndValidate(student, false);
        if (repository.findByEmail(student.getEmail()).isPresent()) {
            throw new DuplicateEmailException(student.getEmail());
        }
        return repository.save(student);
    }

    @Override
    public List<Student> createBatchAtomically(List<Student> students) {
        if (students == null || students.isEmpty()) {
            throw new StudentValidationException("At least one student is required");
        }
        for (Student student : students) {
            normalizeAndValidate(student, false);
        }
        // Deliberately let the database enforce cross-row uniqueness inside this one transaction.
        // This makes the rollback experiment observable without partial commits.
        return repository.saveAllAtomically(students);
    }

    @Override
    public List<Student> getAll() {
        return repository.findAll();
    }

    @Override
    public Student getById(Long id) {
        requireId(id);
        return repository.findById(id).orElseThrow(() -> new StudentNotFoundException(id));
    }

    @Override
    public Student getByEmail(String email) {
        String normalized = normalizeEmail(email);
        return repository.findByEmail(normalized)
                .orElseThrow(() -> new StudentValidationException("Student not found by email: " + normalized));
    }

    @Override
    public Student update(Student student) {
        normalizeAndValidate(student, true);
        getById(student.getId());
        repository.findByEmail(student.getEmail())
                .filter(match -> !match.getId().equals(student.getId()))
                .ifPresent(match -> {
                    throw new DuplicateEmailException(student.getEmail());
                });
        return repository.update(student);
    }

    @Override
    public Student updateMarks(Long id, double marks) {
        requireId(id);
        validateMarks(marks);
        if (repository.findById(id).isEmpty()) {
            throw new StudentNotFoundException(id);
        }
        return repository.updateMarks(id, marks);
    }

    @Override
    public void delete(Long id) {
        requireId(id);
        if (!repository.delete(id)) {
            throw new StudentNotFoundException(id);
        }
    }

    @Override
    public List<Student> getPage(int offset, int limit) {
        if (offset < 0) {
            throw new StudentValidationException("Offset must be >= 0");
        }
        if (limit < 1 || limit > 100) {
            throw new StudentValidationException("Limit must be 1..100");
        }
        return repository.findPage(offset, limit);
    }

    private void normalizeAndValidate(Student student, boolean idRequired) {
        if (student == null) {
            throw new StudentValidationException("Student is required");
        }
        if (idRequired) {
            requireId(student.getId());
        }
        student.setEmail(normalizeEmail(student.getEmail()));
        student.setFirstName(requireText(student.getFirstName(), "First name", 80));
        student.setLastName(requireText(student.getLastName(), "Last name", 80));
        validateMarks(student.getMarks());
    }

    private String normalizeEmail(String email) {
        String value = requireText(email, "Email", 120).toLowerCase(Locale.ROOT);
        if (!value.matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")) {
            throw new StudentValidationException("Email format is invalid");
        }
        return value;
    }

    private String requireText(String value, String field, int maxLength) {
        if (value == null || value.isBlank()) {
            throw new StudentValidationException(field + " is required");
        }
        String trimmed = value.trim();
        if (trimmed.length() > maxLength) {
            throw new StudentValidationException(field + " must be at most " + maxLength + " characters");
        }
        return trimmed;
    }

    private void validateMarks(double marks) {
        if (!Double.isFinite(marks) || marks < 0 || marks > 10) {
            throw new StudentValidationException("Marks must be a finite number in range 0..10");
        }
    }

    private void requireId(Long id) {
        if (id == null || id <= 0) {
            throw new StudentValidationException("Student ID must be a positive number");
        }
    }

}
