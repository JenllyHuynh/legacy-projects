package backend.ojt.management_student_java_spring.controllers;

import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import backend.ojt.management_student_java_spring.services.EnrollmentService;
import backend.ojt.management_student_java_spring.utils.annotations.ApiMessage;
import jakarta.validation.constraints.Positive;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@RequestMapping("/api/v1/enrollments")
public class EnrollmentControllerV1 {
    final EnrollmentService enrollmentService;

    @PreAuthorize("hasRole('STUDENT')")
    @PostMapping("/course/{id}")
    @ApiMessage("Enrollment course")
    public ResponseEntity<String> enroll(@Positive @PathVariable("id") Long courseId) {
        this.enrollmentService.enroll(courseId);
        return ResponseEntity.ok("Enrollment successfully!");
    }

    @GetMapping("/report/course/{id}")
    @ApiMessage("Get report course enroll")
    public ResponseEntity<?> report(@Positive @PathVariable("id") Long courseId, Pageable pageable) {
        return ResponseEntity.ok(this.enrollmentService.report(courseId, pageable));
    }

}
