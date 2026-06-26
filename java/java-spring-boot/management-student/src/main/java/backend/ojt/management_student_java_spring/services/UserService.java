package backend.ojt.management_student_java_spring.services;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.zip.DataFormatException;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import backend.ojt.management_student_java_spring.domain.dto.request.ImportUserRow;
import backend.ojt.management_student_java_spring.domain.dto.request.RequestRegister;
import backend.ojt.management_student_java_spring.domain.dto.request.RequestUserInfo;
import backend.ojt.management_student_java_spring.domain.dto.res.ResImportUser;
import backend.ojt.management_student_java_spring.domain.entity.Lecturer;
import backend.ojt.management_student_java_spring.domain.entity.Student;
import backend.ojt.management_student_java_spring.domain.entity.User;
import backend.ojt.management_student_java_spring.repositories.LecturerRepository;
import backend.ojt.management_student_java_spring.repositories.StudentRepository;
import backend.ojt.management_student_java_spring.repositories.UserRepository;
import backend.ojt.management_student_java_spring.utils.constains.UserGender;
import backend.ojt.management_student_java_spring.utils.constains.UserRole;
import backend.ojt.management_student_java_spring.utils.constains.UserStatus;
import backend.ojt.management_student_java_spring.utils.exceptions.AccessToResourceException;
import backend.ojt.management_student_java_spring.utils.exceptions.ConflictDataException;
import backend.ojt.management_student_java_spring.utils.exceptions.RequestErrorException;
import backend.ojt.management_student_java_spring.utils.exceptions.ResourceNotFoundException;
import backend.ojt.management_student_java_spring.utils.regex.UserRegex;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class UserService {

    final UserRepository userRepository;
    final StudentService studentService;
    final LecturerService lecturerService;
    final PasswordEncoder passwordEncoder;
    final LecturerRepository lecturerRepository;
    final StudentRepository studentRepository;
    private static final Pattern EMAIL_REGEX = Pattern.compile(UserRegex.EMAIL_PATTERN);
    private static final Pattern PHONE_REGEX = Pattern.compile(UserRegex.PHONE_PATTERN);

    /**
     * create lecturer account at admin
     * 
     * @param request
     * @return
     **/
    public Long createLecturer(RequestRegister request) {
        var email = request.getEmail().toLowerCase();
        if (this.userRepository.existsByEmailAccount(email)) {
            throw new ConflictDataException("Email already exist!");
        }
        if (this.userRepository.existsByNumberPhone(request.getPhone())) {
            throw new ConflictDataException("Phone already exist!");
        }

        var user = this.userRepository.save(User.builder()
                .email(email)
                .gender(UserGender.valueOf(request.getGender()))
                .name(request.getName())
                .password(this.passwordEncoder.encode(request.getPassword()))
                .phone(request.getPhone())
                .role(UserRole.LECTURER)
                .status(UserStatus.ACTIVE)
                .build());
        this.lecturerService.createLecturer(user);
        return user.getId();
    }

    /**
     * update base info user account such as name, phone, gender
     * 
     * @param request
     **/
    public void updateInfo(RequestUserInfo request) {
        var user = this.userRepository.findById(request.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found!"));

        if (user.getStatus().equals(UserStatus.INACTIVE)) {
            throw new AccessToResourceException("You do not have permission!");

        }
        if (!user.getRole().equals(UserRole.STUDENT)) {
            throw new AccessToResourceException("You do not have permission!");
        }
        if (request.getGender() != null) {
            user.setGender(UserGender.valueOf(request.getGender()));
        }
        if (request.getName() != null && !request.getName().isBlank()) {
            user.setName(request.getName());
        }
        if (request.getPhone() != null && !request.getPhone().isBlank()) {
            if (!PHONE_REGEX.matcher(request.getPhone()).matches()) {
                throw new RequestErrorException("Phone invalid format!");
            }
            if (this.userRepository.existsByPhoneAndIdNot(request.getPhone(), request.getId())) {
                throw new RequestErrorException("Phone already exist!");
            }
            user.setPhone(request.getPhone());
        }
        this.userRepository.save(user);
    }

    /**
     * import file .xlsx include with type
     * email
     * name
     * phone
     * gender
     * password
     * confirm password
     * role
     * 
     * @param file
     * @throws IOException
     * @throws DataFormatException
     **/
    @Transactional
    public ResImportUser importUsers(MultipartFile file) throws IOException, DataFormatException {
        if (file.getOriginalFilename() == null || !file.getOriginalFilename().endsWith(".xlsx")) {
            throw new RequestErrorException("File invalid, type file must be .xlsx");
        }
        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
            var sheet = workbook.getSheetAt(0);
            if (sheet.getPhysicalNumberOfRows() <= 1) {
                throw new DataFormatException("File empty!");
            }
            Map<String, Integer> emailMap = new LinkedHashMap<>();
            var users = new ArrayList<User>();
            var errors = new ArrayList<ResImportUser.Error>();
            // excel to dto
            var rows = this.parseSheet(sheet);
            for (ImportUserRow r : rows) {
                var isValid = this.validateRow(r, errors);

                // valid row to entity
                if (isValid) {
                    var email = r.email().trim().toLowerCase();
                    emailMap.putIfAbsent(email, r.rowNum());
                    users.add(this.rowToEntity(r));

                }

            }
            // check DB duplicate
            Set<String> dbExist = userRepository.existEmails(emailMap.keySet());
            if (!dbExist.isEmpty()) {
                for (var x : dbExist) {
                    errors.add(this.error(emailMap.get(x), "Email", x + " already exists"));
                }
            }
            users.removeIf(user -> dbExist.contains(user.getEmail().toLowerCase()));
            var usersSave = this.userRepository.saveAllAndFlush(users);
            var lecturers = new ArrayList<Lecturer>();
            var students = new ArrayList<Student>();
            // save to student and lecuturer
            for (var x : usersSave) {
                if (x.getRole() == UserRole.STUDENT) {
                    var student = Student.builder()
                            .studentCode(this.studentService.generateStudentCode(x.getId()))
                            .user(x)
                            .build();
                    students.add(student);
                }
                if (x.getRole() == UserRole.LECTURER) {
                    var lecturer = Lecturer.builder()
                            .lecturerCode(this.lecturerService.generateLecturerCode(x.getId()))
                            .user(x)
                            .build();
                    lecturers.add(lecturer);
                }
            }
            if (!lecturers.isEmpty()) {
                this.lecturerRepository.saveAll(lecturers);

            }
            if (!students.isEmpty()) {
                this.studentRepository.saveAll(students);
            }
            return ResImportUser.builder().errors(errors).failedRows(rows.size() - usersSave.size())
                    .successRows(usersSave.size()).total(rows.size()).build();

        }

    }

    /**
     * check valid row from file sush as empty, invalid format
     * 
     * @param r
     * @param errors
     * @return
     **/
    private boolean validateRow(ImportUserRow r, List<ResImportUser.Error> errors) {
        boolean check = true, checkEmail = true, checkName = true, checkPhone = true, checkGender = true,
                checkRole = true, checkPasword = true, checkConfirmPassword = true;
        if (r.email().isBlank()) {
            errors.add(this.error(r.rowNum(), "Email", "empty"));
            check = false;
            checkEmail = false;
        }
        if (!EMAIL_REGEX.matcher(r.email()).matches() && checkEmail) {
            errors.add(this.error(r.rowNum(), "Email", "invalid format"));
            check = false;

        }
        if (r.name().isEmpty()) {
            errors.add(this.error(r.rowNum(), "Name", "empty"));
            check = false;
            checkName = false;

        }

        if (r.name().length() < 2 && checkName) {
            errors.add(this.error(r.rowNum(), "Name", "too short"));
            check = false;

        }
        if (r.phone().isEmpty()) {
            errors.add(this.error(r.rowNum(), "Phone", "empty"));
            check = false;
            checkPhone = false;

        }
        if (!PHONE_REGEX.matcher(r.phone()).matches() && checkPhone) {
            errors.add(this.error(r.rowNum(), "Phone", "invalid"));
            check = false;

        }
        if (r.gender().isEmpty()) {
            errors.add(this.error(r.rowNum(), "Gender", "empty"));
            check = false;
            checkGender = false;

        }
        if (checkGender) {
            try {
                UserGender.valueOf(r.gender().toUpperCase());
            } catch (Exception e) {
                errors.add(this.error(r.rowNum(), "Gender", "invalid"));
                check = false;

            }
        }

        if (r.role().isEmpty()) {
            errors.add(this.error(r.rowNum(), "Role", "empty"));
            check = false;
            checkRole = false;

        }
        if (checkRole) {
            try {
                UserRole.valueOf(r.role().toUpperCase());
            } catch (Exception e) {
                errors.add(this.error(r.rowNum(), "Role", "invalid"));
                check = false;

            }
        }

        if (r.password().isEmpty()) {
            errors.add(this.error(r.rowNum(), "Password", "empty"));
            check = false;
            checkPasword = false;
        }
        if (r.confirmPassword().isEmpty()) {
            errors.add(this.error(r.rowNum(), "ConfirmPassword", "empty"));
            check = false;
            checkConfirmPassword = false;
        }

        if (r.password().length() < 6 && checkPasword) {
            errors.add(this.error(r.rowNum(), "Password", "less than 6 characters"));
            check = false;

        }
        if (r.confirmPassword().length() < 6 && checkConfirmPassword) {
            errors.add(this.error(r.rowNum(), "Confirm password", "less than 6 characters"));
            check = false;

        }
        if (!r.password().equals(r.confirmPassword()) && checkPasword && checkConfirmPassword) {
            errors.add(this.error(r.rowNum(), "Password", "not match"));
            check = false;

        }
        return check;
    }

    /**
     * convert from excel to ImportUserRow DTO
     * 
     * @param sheet
     * @return
     * @throws DataFormatException
     **/
    private List<ImportUserRow> parseSheet(Sheet sheet) throws DataFormatException {
        List<ImportUserRow> list = new ArrayList<>();
        DataFormatter f = new DataFormatter();
        for (int i = 1; i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            if (row == null) {
                continue;
            }
            list.add(new ImportUserRow(
                    row.getRowNum() + 1,
                    f.formatCellValue(row.getCell(0)).trim(),
                    f.formatCellValue(row.getCell(1)).trim(),
                    f.formatCellValue(row.getCell(2)).trim(),
                    f.formatCellValue(row.getCell(3)).trim(),
                    f.formatCellValue(row.getCell(4)).trim(),
                    f.formatCellValue(row.getCell(5)).trim(),
                    f.formatCellValue(row.getCell(6)).trim()));
        }

        return list;
    }

    /**
     * convert from import user data to entity
     * 
     * @param r
     * @return
     **/
    private User rowToEntity(ImportUserRow r) {
        return User.builder()
                .email(r.email().trim().toLowerCase())
                .gender(UserGender.valueOf(r.gender().trim().toUpperCase()))
                .name(r.name().trim())
                .phone(r.phone().trim())
                .status(UserStatus.ACTIVE)
                .password(this.passwordEncoder.encode(r.password()))
                .role(UserRole.valueOf(r.role().toUpperCase()))
                .build();
    }

    /**
     * return response format error
     * 
     * @param row
     * @param field
     * @param msg
     * @return
     **/
    private ResImportUser.Error error(Integer row, String field, String msg) {
        return ResImportUser.Error.builder()
                .row(row)
                .field(field)
                .message(msg)
                .build();
    }

    /**
     * Export all users to excel file
     * 
     * @return ByteArrayInputStream
     * @throws IOException
     **/
    public ByteArrayInputStream exportUsers() throws IOException {
        List<Student> users = this.studentRepository.findAll();
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Users");

            // Header
            Row headerRow = sheet.createRow(0);
            String[] columns = { "ID", "Email", "Name", "Phone", "Gender", "Student Code", "Credits", "Major",
                    "Status" };
            for (int i = 0; i < columns.length; i++) {
                org.apache.poi.ss.usermodel.Cell cell = headerRow.createCell(i);
                cell.setCellValue(columns[i]);
            }

            // Data
            int rowIdx = 1;
            for (Student student : users) {
                Row row = sheet.createRow(rowIdx++);
                var user = student.getUser();
                row.createCell(0).setCellValue(user.getId());
                row.createCell(1).setCellValue(user.getEmail());
                row.createCell(2).setCellValue(user.getName());
                row.createCell(3).setCellValue(user.getPhone() != null ? user.getPhone() : "");
                row.createCell(4).setCellValue(user.getGender() != null ? user.getGender().name() : "");
                row.createCell(5).setCellValue(student.getStudentCode());
                row.createCell(6).setCellValue(student.getCountCredits());
                row.createCell(7).setCellValue(student.getMajor() != null ? student.getMajor().name() : "");
                // row.createCell(5).setCellValue(user.getRole() != null ? user.getRole().name()
                // : "");
                row.createCell(8).setCellValue(user.getStatus() != null ? user.getStatus().name() : "");
            }

            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());
        }
    }
}
