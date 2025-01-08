package viet.io.threadsbe.utils.enums;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
    // Authentication & Authorization
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "Unauthorized access"),          // 401
    INVALID_CREDENTIALS(HttpStatus.UNAUTHORIZED, "Invalid credentials"),   // 401
    TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "Token expired"),               // 401
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "Invalid token"),               // 401
    FORBIDDEN(HttpStatus.FORBIDDEN, "Forbidden"),                          // 403

    // User-related
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "User not found"),
    USER_NOT_VERIFIED(HttpStatus.CONFLICT, "User not verify"),            // 404
    USER_ALREADY_EXISTS(HttpStatus.CONFLICT, "User already exists"),       // 409
    INVALID_USERNAME(HttpStatus.BAD_REQUEST, "Invalid username"),          // 400
    INVALID_EMAIL(HttpStatus.BAD_REQUEST, "Invalid email"),                // 400
    WEAK_PASSWORD(HttpStatus.BAD_REQUEST, "Weak password"),                // 400

    // Resource errors
    NOT_FOUND(HttpStatus.NOT_FOUND, "Resource not found"),                 // 404
    CONFLICT(HttpStatus.CONFLICT, "Conflict"),                             // 409
    ALREADY_EXISTS(HttpStatus.CONFLICT, "Resource already exists"),        // 409

    // Validation
    VALIDATION_ERROR(HttpStatus.BAD_REQUEST, "Validation error"),          // 400
    BAD_REQUEST(HttpStatus.BAD_REQUEST, "Bad request"),                    // 400

    // Server errors
    INTERNAL_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error"), // 500
    SERVICE_UNAVAILABLE(HttpStatus.SERVICE_UNAVAILABLE, "Service unavailable"), // 503

    // Business logic
    INVALID_OPERATION(HttpStatus.BAD_REQUEST, "Invalid operation"),        // 400
    RATE_LIMIT_EXCEEDED(HttpStatus.TOO_MANY_REQUESTS, "Rate limit exceeded"); // 429

    private final HttpStatus status;
    private final String message;

    // Constructor
    ErrorCode(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }
}
