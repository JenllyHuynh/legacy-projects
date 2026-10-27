package fu.edu.sba301.ch18.repository;

import fu.edu.sba301.ch18.dao.StudentDAO;
import fu.edu.sba301.ch18.entity.Student;

import java.util.List;
import java.util.Optional;

public class StudentRepository implements IStudentRepository {
    private final StudentDAO dao;

    public StudentRepository(StudentDAO dao) {
        this.dao = dao;
    }

    @Override
    public Student save(Student student) {
        return dao.save(student);
    }

    @Override
    public List<Student> saveAllAtomically(List<Student> students) {
        return dao.saveAllAtomically(students);
    }

    @Override
    public List<Student> findAll() {
        return dao.findAll();
    }

    @Override
    public Optional<Student> findById(Long id) {
        return dao.findById(id);
    }

    @Override
    public Optional<Student> findByEmail(String email) {
        return dao.findByEmail(email);
    }

    @Override
    public Student update(Student student) {
        return dao.update(student);
    }

    @Override
    public Student updateMarks(Long id, double marks) {
        return dao.updateMarks(id, marks);
    }

    @Override
    public boolean delete(Long id) {
        return dao.delete(id);
    }

    @Override
    public List<Student> findPage(int offset, int limit) {
        return dao.findPage(offset, limit);
    }
}
