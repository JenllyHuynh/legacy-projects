package backend.ojt.management_student_java_spring.domain.dto.res;

import lombok.Getter;
import lombok.Setter;

/**
 * Format data response
 **/
@Getter
@Setter
public class RestResponse<T> {
    private int statusCode;
    private String error;
    private Object message;
    private T data;
}
