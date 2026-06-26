package backend.ojt.management_student_java_spring.domain.dto.res;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ResImportUser {
    Integer total;
    @JsonProperty("success_rows")
    Integer successRows;
    List<ResImportUser.Error> errors;
    @JsonProperty("failed_rows")
    Integer failedRows;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class Error {
        Integer row;
        String field;
        String message;
    }
}
