package backend.ojt.management_student_java_spring.controllers;

import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import backend.ojt.management_student_java_spring.domain.dto.request.RequestRegister;
import backend.ojt.management_student_java_spring.domain.dto.request.RequestStudent;
import backend.ojt.management_student_java_spring.domain.dto.request.RequestUserInfo;
import backend.ojt.management_student_java_spring.services.AuthService;
import backend.ojt.management_student_java_spring.services.StudentService;
import backend.ojt.management_student_java_spring.services.UserService;
import backend.ojt.management_student_java_spring.utils.annotations.ApiMessage;
import backend.ojt.management_student_java_spring.utils.constains.StudentMajor;
import backend.ojt.management_student_java_spring.utils.constains.UserGender;
import backend.ojt.management_student_java_spring.utils.exceptions.RequestErrorException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@RestController
@RequestMapping("/api/v1/students")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class StudentControllerV1 {
    final StudentService studentService;
    final AuthService authService;
    final UserService userService;

    /**
     * student register major
     * 
     * @param request
     * @return
     **/
    @PreAuthorize("hasRole('STUDENT')")
    @PatchMapping("/me/major")
    @ApiMessage("Student register major")
    public ResponseEntity<?> registerMajor(@RequestBody RequestStudent request) {
        var major = request.major();
        try {
            StudentMajor.valueOf(request.major().toString());
        } catch (Exception e) {
            throw new RequestErrorException("Major invalid format!");

        }
        this.studentService.registerMajor(major);
        return ResponseEntity.ok("Register major successfully!");
    }

    /**
     * create student at admin
     * 
     * @param request
     * @return
     **/
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    @ApiMessage("Create student by admin")
    public ResponseEntity<Long> createStudent(@Valid @RequestBody RequestRegister request) {
        if (request.getGender() != null) {
            try {
                UserGender.valueOf(request.getGender());
            } catch (Exception ex) {
                throw new RequestErrorException("Gender invalid format!");
            }
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(this.authService.register(request));
    }

    /**
     * update name, gender, phone student
     * 
     * @param request
     * @return
     **/
    @PreAuthorize("hasAnyRole('ADMIN', 'STUDENT')")
    @PatchMapping
    @ApiMessage("Update base information student")
    public ResponseEntity<?> update(@Valid @RequestBody RequestUserInfo request) {
        try {
            UserGender.valueOf(request.getGender());
        } catch (Exception ex) {
            throw new RequestErrorException("Gender invalid format");
        }
        this.userService.updateInfo(request);
        return ResponseEntity.ok("Update student successfully!");
    }

    /**
     * delete student by admin
     * 
     * @param id
     * @return
     **/
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    @ApiMessage("Delete student by id")
    public ResponseEntity<?> delete(@Positive @PathVariable("id") Long id) {
        this.studentService.delete(id);
        return ResponseEntity.ok("Delete student successfully!");
    }

    /**
     * fetch all student
     * 
     * @param pageable
     * @param q
     * @return
     **/
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    @ApiMessage("Fetch all student")
    public ResponseEntity<?> fetchAll(Pageable pageable, @RequestParam(name = "q", required = false) String q) {
        return ResponseEntity.ok(this.studentService.fetchAll(pageable, q != null ? q : ""));
    }

    @GetMapping("/{id}")
    @ApiMessage("Fetch student by id")
    public ResponseEntity<?> fetchById(@Positive @PathVariable("id") Long id) {
        return ResponseEntity.ok(this.studentService.fetchById(id));
    }

}
