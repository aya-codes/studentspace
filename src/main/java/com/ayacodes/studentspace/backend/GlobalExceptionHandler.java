package com.ayacodes.studentspace.backend;

import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Arrays;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<String> handleInvalidTopic(HttpMessageNotReadableException ex) {
        return ResponseEntity
                .badRequest()
                .body("Invalid topic. Valid options are: " + Arrays.toString(Topic.values()));
    }
}
