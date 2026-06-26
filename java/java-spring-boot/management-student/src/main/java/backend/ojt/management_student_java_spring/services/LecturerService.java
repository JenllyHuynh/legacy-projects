package backend.ojt.management_student_java_spring.services;

import java.time.LocalDateTime;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import backend.ojt.management_student_java_spring.domain.dto.request.RequestLecturer;
import backend.ojt.management_student_java_spring.domain.dto.res.ResLecturer;
import backend.ojt.management_student_java_spring.domain.dto.res.ResultPagination;
import backend.ojt.management_student_java_spring.domain.entity.Lecturer;
import backend.ojt.management_student_java_spring.domain.entity.User;
import backend.ojt.management_student_java_spring.repositories.CourseRepository;
import backend.ojt.management_student_java_spring.repositories.LecturerRepository;
import backend.ojt.management_student_java_spring.repositories.UserRepository;
import backend.ojt.management_student_java_spring.utils.constains.LecturerAcademicTitle;
import backend.ojt.management_student_java_spring.utils.constains.LecturerDepartment;
import backend.ojt.management_student_java_spring.utils.constains.UserRole;
import backend.ojt.management_student_java_spring.utils.constains.UserStatus;
import backend.ojt.management_student_java_spring.utils.exceptions.AccessToResourceException;
import backend.ojt.management_student_java_spring.utils.exceptions.RequestErrorException;
import backend.ojt.management_student_java_spring.utils.exceptions.ResourceNotFoundException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class LecturerService {
        final LecturerRepository lecturerRepository;
        final CourseRepository courseRepository;
        final UserRepository userRepository;

        /**
         * init lecturer code
         * 
         * @param user
         **/
        public String generateLecturerCode(Long userId) {
                String year = String.format("%02d",
                                LocalDateTime.now().getYear() % 100);
                String lecturerCode = "L"
                                + year
                                + String.format("%06d", userId);
                return lecturerCode;
        }

        /**
         * init lecturer
         * 
         * @param user
         **/
        public void createLecturer(User user) {
                this.lecturerRepository.save(
                                Lecturer.builder()
                                                .lecturerCode(this.generateLecturerCode(user.getId()))
                                                .user(user)
                                                .build());
        }

        /**
         * update department or academic title lecturer
         * 
         * @param requestLecturer
         **/
        @Transactional
        public void registerInfoLecturer(RequestLecturer requestLecturer) {
                var lecturer = this.lecturerRepository.findWithDetailById(requestLecturer.id())
                                .orElseThrow(() -> new ResourceNotFoundException("Lecturer not found!"));
                if (lecturer.getUser().getStatus().equals(UserStatus.INACTIVE)) {
                        throw new AccessToResourceException("You do not have permission!");

                }
                if (!lecturer.getUser().getRole().equals(UserRole.LECTURER)) {
                        throw new AccessToResourceException("You do not have permission!");

                }
                if (requestLecturer.academicTitle() != null) {
                        lecturer.setAcademicTitle(LecturerAcademicTitle.valueOf(requestLecturer.academicTitle()));
                }
                if (requestLecturer.department() != null) {
                        lecturer.setDepartment(LecturerDepartment.valueOf(requestLecturer.department()));
                }
        }

        /**
         * get information lecturer by lecturer id
         * 
         * @param id
         * @return
         **/
        public ResLecturer getLecturerById(Long id) {
                var lecturer = this.lecturerRepository.findWithDetailIncludeCourseById(id, UserStatus.ACTIVE,
                                UserRole.LECTURER)
                                .orElseThrow(() -> new ResourceNotFoundException("Lecturer not found!"));
                return ResLecturer.builder()
                                .academicTitle(lecturer.getAcademicTitle())
                                .email(lecturer.getUser().getEmail())
                                .name(lecturer.getUser().getName())
                                .gender(lecturer.getUser().getGender())
                                .id(lecturer.getUserId())
                                .lecturerCode(lecturer.getLecturerCode())
                                .department(lecturer.getDepartment())
                                .courses(lecturer.getCourses().stream().map(x -> {
                                        return ResLecturer.ResCourse.builder()
                                                        .id(x.getId())
                                                        .name(x.getName())
                                                        .courseCode(x.getCourseCode())
                                                        .build();
                                }).toList()).build();

        }

        /**
         * fetch all data lecturer
         * 
         * @param pageable
         * @Param q email or lecturer code
         * @return
         **/
        public ResultPagination fetchAll(Pageable pageable, String q) {
                var res = new ResultPagination();
                var meta = new ResultPagination.Meta();
                var page = this.lecturerRepository.fetchAll(pageable, q);
                meta.setPage(pageable.getPageNumber() + 1);
                meta.setPageSize(pageable.getPageSize());
                meta.setPages(page.getTotalPages());
                meta.setTotal(page.getTotalElements());
                res.setMeta(meta);
                res.setResult(page.getContent());
                return res;
        }

        /**
         * delete lecturer and user account
         * 
         * @param id
         **/
        public void deleteById(Long id) {
                var lecturer = this.lecturerRepository.findWithDetailById(id)
                                .orElseThrow(() -> new ResourceNotFoundException("Lecturer not found!"));
                if (!lecturer.getUser().getRole().equals(UserRole.LECTURER)) {
                        throw new AccessToResourceException("You do not have permission!");
                }
                if (this.courseRepository.existsByLecturerId(id)) {
                        throw new RequestErrorException(
                                        "It is not possible to delete a lecturer who already has a course!");
                } else {
                        this.lecturerRepository.delete(lecturer);
                        this.userRepository.delete(lecturer.getUser());
                }
        }

}
