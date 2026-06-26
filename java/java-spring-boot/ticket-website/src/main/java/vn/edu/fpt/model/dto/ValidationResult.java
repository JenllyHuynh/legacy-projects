package vn.edu.fpt.model.dto;

public class ValidationResult {
    private String errorTitle;
    private String message;

    public ValidationResult() {
    }

    public ValidationResult(String errorTitle, String message) {
        this.errorTitle = errorTitle;
        this.message = message;
    }

    public String getErrorTitle() {
        return errorTitle;
    }

    public void setErrorTitle(String errorTitle) {
        this.errorTitle = errorTitle;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
