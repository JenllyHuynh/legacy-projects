package backend.ojt.management_student_java_spring.services;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import backend.ojt.management_student_java_spring.domain.dto.request.RequestCourse;
import backend.ojt.management_student_java_spring.domain.dto.res.CourseProjection;
import backend.ojt.management_student_java_spring.domain.dto.res.ResultPagination;
import backend.ojt.management_student_java_spring.domain.entity.Course;
import backend.ojt.management_student_java_spring.repositories.CourseRepository;
import backend.ojt.management_student_java_spring.repositories.LecturerRepository;
import backend.ojt.management_student_java_spring.utils.constains.CourseSemester;
import backend.ojt.management_student_java_spring.utils.constains.CourseStatus;
import backend.ojt.management_student_java_spring.utils.exceptions.AccessToResourceException;
import backend.ojt.management_student_java_spring.utils.exceptions.ConflictDataException;
import backend.ojt.management_student_java_spring.utils.exceptions.RequestErrorException;
import backend.ojt.management_student_java_spring.utils.exceptions.ResourceNotFoundException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CourseService {
    final CourseRepository courseRepository;
    final LecturerRepository lecturerRepository;

    /**
     * create course
     * 
     * @param request
     * @return
     **/
    public CourseProjection createCourse(RequestCourse request) {
        if (this.courseRepository.existsByCourseCodeIgnoreCase(request.getCourseCode())) {
            throw new ConflictDataException("Course code already exist!");
            // throw new RequestErrorException("Course code aleary exists!");
        }
        var year = request.getEnrollStartDate().getYear();
        var month = request.getEnrollStartDate().getMonth().getValue();
        request.setYear(year);
        request.setSemester(this.getSemesterByMonth(month));

        return this.toRes(this.courseRepository.save(this.toEntity(request)).getId());

    }

    /**
     * convert request to entity
     * 
     * @param request
     * @return
     **/
    public Course toEntity(RequestCourse request) {
        var lecturer = this.lecturerRepository.findById(request.getLecturerId())
                .orElseThrow(() -> new ResourceNotFoundException("Lecturer not found!"));
        return Course.builder()
                .courseCode(request.getCourseCode())
                .credits(request.getCredits())
                .description(request.getDescription())
                .maxStudents(request.getMaxStudents())
                .name(request.getName())
                .enrollEndDate(request.getEnrollEndDate())
                .enrollStartDate(request.getEnrollStartDate())
                .semester(request.getSemester())
                .lecturer(lecturer)
                .year(request.getYear())
                .status(CourseStatus.valueOf(request.getStatus()))
                .build();
    }

    /**
     * to response
     * 
     * @param courseId
     * @return
     **/
    public CourseProjection toRes(Long courseId) {
        return this.courseRepository.findResById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found!"));
    }

    /**
     * generate semester
     * 
     * @param month
     * @return
     **/
    public CourseSemester getSemesterByMonth(int month) {
        return (month > 0 && month < 6) ? CourseSemester.SPRING
                : (month > 5 && month < 9) ? CourseSemester.SUMMER
                        : (month > 8 && month <= 12) ? CourseSemester.FALL
                                : null;
    }

    /**
     * update course credits, lecturer, max student, description, name, semester
     * 
     * @param request
     */
    public CourseProjection updateCourse(RequestCourse request) {
        var course = this.courseRepository.findById(request.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Course not found!"));
        if (course.getStatus().equals(CourseStatus.ACTIVE)) {
            throw new AccessToResourceException("The course was working!");
        }
        // check slot
        if (course.getCurrentStudents() > request.getMaxStudents()) {
            throw new RequestErrorException("Currently, " + course.getCurrentStudents()
                    + " students have registered, it's impossible to change the number to something smaller.");
        }
        var lecturer = this.lecturerRepository.findById(request.getLecturerId())
                .orElseThrow(() -> new ResourceNotFoundException("Lecturer not found!"));
        course.setCredits(request.getCredits());
        course.setMaxStudents(request.getMaxStudents());
        course.setDescription(request.getDescription());
        course.setLecturer(lecturer);
        course.setName(request.getName());
        var year = request.getEnrollStartDate().getYear();
        var month = request.getEnrollStartDate().getMonth().getValue();
        course.setYear(year);
        course.setSemester(this.getSemesterByMonth(month));
        course.setStatus(CourseStatus.valueOf(request.getStatus()));
        course.setEnrollEndDate(request.getEnrollEndDate());
        course.setEnrollStartDate(request.getEnrollStartDate());
        return this.toRes(this.courseRepository.save(course).getId());
    }

    /**
     * delete course
     * 
     * @param id
     */
    public void delete(Long id) {
        var course = this.courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found!"));
        if (course.getStatus().equals(CourseStatus.ACTIVE)) {
            throw new AccessToResourceException("The course was working!");
        }
        this.courseRepository.delete(course);

    }

    public CourseProjection findById(Long id) {
        return this.courseRepository.findResById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found!"));
    }

    public ResultPagination fetchAll(Pageable pageable, String q) {
        var res = new ResultPagination();
        var meta = new ResultPagination.Meta();
        meta.setPage(pageable.getPageNumber() + 1);
        meta.setPageSize(pageable.getPageSize());
        var page = this.courseRepository.fetchAll(pageable, q);
        meta.setPages(page.getTotalPages());
        meta.setTotal(page.getTotalElements());
        res.setMeta(meta);
        res.setResult(page.getContent());
        return res;
    }

}
