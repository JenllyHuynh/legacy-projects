package backend.ojt.management_student_java_spring.domain.entity;

import java.time.LocalDateTime;
import java.util.List;

import backend.ojt.management_student_java_spring.utils.constains.CourseSemester;
import backend.ojt.management_student_java_spring.utils.constains.CourseStatus;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Entity
@Table(name = "courses")
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Course extends BaseEntity {
    @Column(unique = true, nullable = false)

    String courseCode;
    @Column(nullable = false)

    String name;
    String description;
    Integer credits;
    Integer maxStudents;
    @Builder.Default
    Integer currentStudents = 0;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lecturer_id")
    Lecturer lecturer;
    @Enumerated(EnumType.STRING)
    CourseSemester semester;
    Integer year;
    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
    List<Enrollment> enrollments;
    @Version
    Long version;
    @Column(nullable = false)
    LocalDateTime enrollStartDate;
    @Column(nullable = false)
    LocalDateTime enrollEndDate;
    @Enumerated(EnumType.STRING)
    CourseStatus status;
}
