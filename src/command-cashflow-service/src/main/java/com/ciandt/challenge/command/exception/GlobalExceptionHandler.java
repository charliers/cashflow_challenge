package com.ciandt.challenge.command.exception;

import com.ciandt.challenge.shared.model.dto.ErrorDetail;
import com.ciandt.challenge.shared.model.dto.ErrorResponse;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex) {
        ErrorResponse error = new ErrorResponse(
                "VALIDATION_ERROR",
             "Erro de validação nos campos da requisição.",
                     ex.getBindingResult().getFieldErrors().stream().map(fe -> {
            ErrorDetail d = new ErrorDetail(fe.getField(), fe.getDefaultMessage());
            return d;
        }).collect(Collectors.toList()));
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(error);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolation(ConstraintViolationException ex) {
        ErrorResponse error = new ErrorResponse(
                "BAD_REQUEST",
                     "Parâmetros inválidos na requisição.",
                     ex.getConstraintViolations().stream().map(cv -> {
            ErrorDetail d = new ErrorDetail(cv.getPropertyPath().toString(), cv.getMessage());
            return d;
        }).collect(Collectors.toList()));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneric(Exception ex) {
        ErrorResponse error = new ErrorResponse(
            "INTERNAL_SERVER_ERROR",
         "Ocorreu um erro inesperado. Tente novamente mais tarde.",
           null);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}
