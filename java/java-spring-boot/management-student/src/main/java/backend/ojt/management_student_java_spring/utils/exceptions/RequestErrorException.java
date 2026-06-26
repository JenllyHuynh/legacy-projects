package backend.ojt.management_student_java_spring.utils.exceptions;

public class RequestErrorException extends RuntimeException {
    public RequestErrorException(String message) {
        super(message);
    }
}
