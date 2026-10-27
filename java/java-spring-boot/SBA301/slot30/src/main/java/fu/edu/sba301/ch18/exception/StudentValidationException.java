package fu.edu.sba301.ch18.exception;

public class StudentValidationException extends IllegalArgumentException {
    public StudentValidationException(String message) {
        super(message);
    }
}
