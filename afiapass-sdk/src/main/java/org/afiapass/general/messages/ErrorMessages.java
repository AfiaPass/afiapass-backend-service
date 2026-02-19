package org.afiapass.general.messages;

public final class ErrorMessages {

    private ErrorMessages() {}

    public static final String USER_NOT_FOUND = "User not found.";
    public static final String EMAIL_ALREADY_EXISTS = "Email already registered.";
    public static final String INVALID_CREDENTIALS = "Invalid username or password.";
    public static final String EMAIL_NOT_VERIFIED = "Email not verified. Please verify before login.";
    public static final String PASSWORD_MISMATCH = "Passwords do not match.";
    public static final String TOKEN_EXPIRED = "Refresh token expired.";
    public static final String INVALID_TOKEN = "Invalid token.";
    public static final String EMAIL_FAILED = "Failed to send email.";
    public static final String OPERATION_FAILED = "Operation failed.";
    public static final String ACCESS_DENIED = "You do not have permission to perform this action.";
    public static final String INCORRECT_PIN_FORMAT = "PIN must be exactly 4 digits";
    public static final String USER_ALREADY_EXISTS ="User already exists in db";
    public static final String USER_NOT_AUTHENTICATED = "User not authenticated";
}
