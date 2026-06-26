package backend.ojt.management_student_java_spring.controllers;

import java.time.LocalDateTime;

import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import backend.ojt.management_student_java_spring.domain.dto.request.RequestCourse;
import backend.ojt.management_student_java_spring.services.CourseService;
import backend.ojt.management_student_java_spring.utils.annotations.ApiMessage;
import backend.ojt.management_student_java_spring.utils.constains.CourseStatus;
import backend.ojt.management_student_java_spring.utils.exceptions.RequestErrorException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@RestController
@RequestMapping("/api/v1/courses")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CourseControllerV1 {
    final CourseService courseService;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    @ApiMessage("Create new course with lecturer ID")
    public ResponseEntity<?> createCourse(@Valid @RequestBody RequestCourse request) {
        if (request != null) {
            try {
                CourseStatus.valueOf(request.getStatus());
            } catch (Exception ex) {
                throw new RequestErrorException("Course status invalid format!");
            }
            var now = LocalDateTime.now();
            var startDate = request.getEnrollStartDate();
            var endDate = request.getEnrollEndDate();
            if (startDate.isBefore(now)) {
                throw new RequestErrorException("Enroll start date cannot be in the past!");
            }
            if (endDate.isBefore(startDate)) {
                throw new RequestErrorException("Enroll end date must be after enroll start date!");
            }
            return ResponseEntity.status(HttpStatus.CREATED).body(this.courseService.createCourse(request));
        }
        throw new RequestErrorException("Data request null!");
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping
    @ApiMessage("Update course")
    public ResponseEntity<?> updateCourse(@Valid @RequestBody RequestCourse request) {
        if (request != null) {
            if (request.getId() == null) {
                throw new RequestErrorException("Course ID not found!");
            }
            try {
                CourseStatus.valueOf(request.getStatus());
            } catch (Exception ex) {
                throw new RequestErrorException("Course status invalid format!");
            }
            var now = LocalDateTime.now();
            var startDate = request.getEnrollStartDate();
            var endDate = request.getEnrollEndDate();
            if (startDate.isBefore(now)) {
                throw new RequestErrorException("Enroll start date cannot be in the past!");
            }
            if (endDate.isBefore(startDate)) {
                throw new RequestErrorException("Enroll end date must be after enroll start date!");
            }
            return ResponseEntity.status(HttpStatus.CREATED).body(this.courseService.updateCourse(request));
        }
        throw new RequestErrorException("Data request null!");
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    @ApiMessage("Delete course")
    public ResponseEntity<?> delete(@Positive @PathVariable("id") Long id) {
        this.courseService.delete(id);
        return ResponseEntity.ok("Delete course successfully");
    }

    @GetMapping("/{id}")
    @ApiMessage("Fetch course by id")
    public ResponseEntity<?> fetchById(@Positive @PathVariable("id") Long id) {
        return ResponseEntity.ok(this.courseService.findById(id));
    }

    @GetMapping
    @ApiMessage("Fetch all course")
    public ResponseEntity<?> fetchAll(Pageable pageable, @RequestParam(value = "q", required = false) String q) {
        return ResponseEntity.ok(this.courseService.fetchAll(pageable, q != null ? q : ""));
    }

}
