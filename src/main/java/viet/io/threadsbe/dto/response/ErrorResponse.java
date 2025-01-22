package viet.io.threadsbe.dto.response;

import lombok.*;

@Data
@Builder
public class ErrorResponse {
    private int status;
    private String message;
    private String details;
}
