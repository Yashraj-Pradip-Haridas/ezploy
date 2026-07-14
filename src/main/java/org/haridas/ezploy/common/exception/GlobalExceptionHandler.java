package org.haridas.ezploy.common.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.haridas.ezploy.common.enums.ErrorCode;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.OffsetDateTime;
import java.util.UUID;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private ApiErrorResponse buildError(int status, ErrorCode errorCode, String message, HttpServletRequest request){
        OffsetDateTime timestamp = OffsetDateTime.now();
        return new ApiErrorResponse(timestamp,status, errorCode, message,request.getRequestURI());
    }

    @ExceptionHandler(ProjectNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleProjectNotFound(ProjectNotFoundException ex, HttpServletRequest request){
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(buildError(HttpStatus.NOT_FOUND.value(),ErrorCode.PROJECT_NOT_FOUND,ex.getMessage(),request));
    }
}
