package fu.edu.sba301.ch18.service;

import fu.edu.sba301.ch18.entity.Student;

import java.util.List;

public interface IStudentService {
    Student create(Student student);

    List<Student> createBatchAtomically(List<Student> students);

    List<Student> getAll();

    Student getById(Long id);

    Student getByEmail(String email);

    Student update(Student student);

    Student updateMarks(Long id, double marks);

    void delete(Long id);

    List<Student> getPage(int offset, int limit);
}
