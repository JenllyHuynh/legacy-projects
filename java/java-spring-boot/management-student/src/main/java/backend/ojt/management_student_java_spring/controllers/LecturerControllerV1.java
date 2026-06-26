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

import backend.ojt.management_student_java_spring.domain.dto.request.RequestLecturer;
import backend.ojt.management_student_java_spring.domain.dto.request.RequestRegister;
import backend.ojt.management_student_java_spring.domain.dto.request.RequestUserInfo;
import backend.ojt.management_student_java_spring.domain.dto.res.ResLecturer;
import backend.ojt.management_student_java_spring.domain.dto.res.ResultPagination;
import backend.ojt.management_student_java_spring.services.LecturerService;
import backend.ojt.management_student_java_spring.services.UserService;
import backend.ojt.management_student_java_spring.utils.annotations.ApiMessage;
import backend.ojt.management_student_java_spring.utils.constains.LecturerAcademicTitle;
import backend.ojt.management_student_java_spring.utils.constains.LecturerDepartment;
import backend.ojt.management_student_java_spring.utils.constains.UserGender;
import backend.ojt.management_student_java_spring.utils.exceptions.RequestErrorException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@RestController
@RequestMapping("/api/v1/lecturers")
@FieldDefaults(level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class LecturerControllerV1 {
    final UserService userService;
    final LecturerService lecturerService;

    /**
     * create account lecture base information
     * 
     * @param register
     * @return
     **/
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    @ApiMessage("Create account for lecturer")
    public ResponseEntity<?> createAccountLecturer(@Valid @RequestBody RequestRegister register) {
        if (register.getConfirmPassword().equals(register.getPassword())) {
            return ResponseEntity.status(HttpStatus.CREATED).body(this.userService.createLecturer(register));
        }
        throw new RequestErrorException("Password and confirm password not the same!");
    }

    /**
     * update information such as department, academic title
     * 
     * @param request
     * @return
     **/
    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/skill")
    @ApiMessage("Update academic or department")
    public ResponseEntity<?> updateCoreLecturer(@Valid @RequestBody RequestLecturer request) {
        if (request.academicTitle() != null) {
            try {
                LecturerAcademicTitle.valueOf(request.academicTitle());
            } catch (Exception ex) {
                throw new RequestErrorException("Academic title invalid format!");
            }
        }
        if (request.department() != null) {
            try {
                LecturerDepartment.valueOf(request.department());
            } catch (Exception ex) {
                throw new RequestErrorException("Department invalid format!");
            }
        }
        this.lecturerService.registerInfoLecturer(request);
        return ResponseEntity.ok("Update skill lecturer successfully!");
    }

    /**
     * get lucturer by id
     * 
     * @param id
     * @return
     **/
    @GetMapping("/{id}")
    @ApiMessage("Get information and course by lecturer id")
    public ResponseEntity<ResLecturer> fetchLecturerById(@Positive @PathVariable("id") Long id) {
        return ResponseEntity.ok(this.lecturerService.getLecturerById(id));
    }

    /**
     * fetch all lecturer with pagination
     * 
     * @param pageable
     * @return
     **/
    @GetMapping
    @ApiMessage("Fetch all data lecturer available")
    public ResponseEntity<ResultPagination> fetchAll(Pageable pageable,
            @RequestParam(name = "q", required = false) String q) {
        return ResponseEntity.ok(this.lecturerService.fetchAll(pageable, q != null ? q : ""));
    }

    /**
     * delete lecuture by id
     * 
     * @param id
     * @return
     **/
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    @ApiMessage("Delete lecturer by Id")
    public ResponseEntity<?> delete(@Positive @PathVariable("id") Long id) {
        this.lecturerService.deleteById(id);
        return ResponseEntity.ok("Delete lecturer successfully!");
    }

    /**
     * update name, phone, gender lecturer
     * 
     * @param request
     * @return
     **/
    @PreAuthorize("hasAnyRole('ADMIN', 'LECTURER')")
    @PatchMapping
    @ApiMessage("Update infor lecturer")
    public ResponseEntity<?> updateInfoLecturer(@Valid @RequestBody RequestUserInfo request) {
        if (request.getGender() != null) {
            try {
                UserGender.valueOf(request.getGender());
            } catch (Exception ex) {
                throw new RequestErrorException("Gender invalid format");
            }
        }
        this.userService.updateInfo(request);
        return ResponseEntity.ok("Update info lecturer successfully!");
    }
}
