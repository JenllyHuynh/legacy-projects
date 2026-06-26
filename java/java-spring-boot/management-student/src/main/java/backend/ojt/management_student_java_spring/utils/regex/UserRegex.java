package backend.ojt.management_student_java_spring.utils.regex;

public final class UserRegex {
    public static final String EMAIL_PATTERN = "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@([a-zA-Z0-9]([a-zA-Z0-9-]*[a-zA-Z0-9])?)\\.[a-zA-Z]{2,3}(?:\\.[a-zA-Z]{2,3})*$";
    public static final String PHONE_PATTERN = "^(0|\\+84)[0-9]{9}$";
}
