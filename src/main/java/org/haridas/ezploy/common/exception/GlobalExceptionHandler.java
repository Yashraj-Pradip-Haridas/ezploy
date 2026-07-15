package org.haridas.ezploy.common.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.haridas.ezploy.common.enums.ErrorCode;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.OffsetDateTime;
import java.util.*;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private ApiErrorResponse buildError(HttpStatus status, ErrorCode errorCode, String message,
                                        HttpServletRequest request,
                                        Map<String, String> fieldErrors) {
        OffsetDateTime timestamp = OffsetDateTime.now();
        return new ApiErrorResponse(timestamp, status.value(), errorCode,message, request.getRequestURI(),
                fieldErrors);
    }

    @ExceptionHandler(ProjectNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleProjectNotFound(ProjectNotFoundException ex, HttpServletRequest request){
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(buildError(HttpStatus.NOT_FOUND,ErrorCode.PROJECT_NOT_FOUND,ex.getMessage(),request,
                        Collections.emptyMap()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpServletRequest request){
        LinkedHashMap<String, String> errors = new LinkedHashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> {
            String fieldName = error.getField();
            String errorMessage = error.getDefaultMessage();

//            Map the field name to its validation error message
            errors.put(fieldName, errorMessage);
        });

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                buildError(HttpStatus.BAD_REQUEST,ErrorCode.VALIDATION_ERROR,"Invalid Arguments",request, errors)
        );
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiErrorResponse> handleHttpMessageNotReadable(
            HttpMessageNotReadableException ex,
            HttpServletRequest request
    ){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                buildError(HttpStatus.BAD_REQUEST,ErrorCode.INVALID_REQUEST,"Invalid request body",request,
                        Collections.emptyMap())
        );
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiErrorResponse> handleMethodArgumentTypeMismatch(MethodArgumentTypeMismatchException ex, HttpServletRequest request){
        String message = String.format(
                "Invalid value '%s' for parameter '%s'.",
                ex.getValue(),
                ex.getName()
        );

        Class<?> requiredType = ex.getRequiredType();

        if (requiredType != null && requiredType.isEnum()) {

            String supportedValues = Arrays.stream(requiredType.getEnumConstants())
                    .map(Object::toString)
                    .collect(Collectors.joining(", "));

            message += " Supported values: " + supportedValues + ".";
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                buildError(HttpStatus.BAD_REQUEST,ErrorCode.INVALID_REQUEST,
                        message,request,
                        Collections.emptyMap())
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleException(Exception ex, HttpServletRequest request){
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                buildError(HttpStatus.INTERNAL_SERVER_ERROR,ErrorCode.INTERNAL_SERVER_ERROR,"An unexpected error " +
                                "occurred",
                        request,
                        Collections.emptyMap()));
    }
}
