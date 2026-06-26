package backend.ojt.management_student_java_spring.controllers;

import java.io.IOException;
import java.util.zip.DataFormatException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import java.io.ByteArrayInputStream;

import backend.ojt.management_student_java_spring.domain.dto.res.ResImportUser;
import backend.ojt.management_student_java_spring.services.UserService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserControllerV1 {
    final UserService userService;

    /**
     * import user sush as lecturer, student from excel file (.xlsx)
     * 
     * @param file
     * @return
     * @throws IOException
     * @throws DataFormatException
     **/
    @PreAuthorize("hasRole('ADMIN')")

    @PostMapping("/import")
    public ResponseEntity<ResImportUser> importUsers(@RequestPart("file") MultipartFile file)
            throws IOException, DataFormatException {
        return ResponseEntity.status(HttpStatus.CREATED).body(this.userService.importUsers(file));
    }

    /**
     * export all users to excel file
     * 
     * @return
     * @throws IOException
     **/
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/export")
    public ResponseEntity<InputStreamResource> exportUsers() throws IOException {
        ByteArrayInputStream in = this.userService.exportUsers();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=users.xlsx");

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(new InputStreamResource(in));
    }
}
