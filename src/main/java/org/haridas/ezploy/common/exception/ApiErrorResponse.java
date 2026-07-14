package org.haridas.ezploy.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.haridas.ezploy.common.enums.ErrorCode;

import java.time.OffsetDateTime;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ApiErrorResponse{

    public ApiErrorResponse(OffsetDateTime timestamp, int status ,ErrorCode errorCode, String message, String path) {
        this(timestamp, status, errorCode, message, path, null);
    }
    private OffsetDateTime timestamp;
    private int status;
    private ErrorCode errorCode;
    private String message;
    private String path;
    Map<String, String> fieldErrors;
}
