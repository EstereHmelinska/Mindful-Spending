package com.mindfulspending.exception;


import com.mindfulspending.exception.category.CategoryInUseException;
import com.mindfulspending.exception.category.CategoryNotFoundException;
import com.mindfulspending.exception.category.DuplicateCategoryException;
import com.mindfulspending.exception.spendingPeriod.InvalidSpendingPeriodException;
import com.mindfulspending.exception.spendingPeriod.SpendingPeriodNotFoundException;
import com.mindfulspending.exception.spendingPeriod.SpendingPeriodOverlapException;
import com.mindfulspending.exception.user.EmailAlreadyExistsException;
import com.mindfulspending.exception.user.UserNotFoundException;
import org.apache.coyote.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<Map<String, String>> handleEmailExists(EmailAlreadyExistsException ex){
        Map<String, String> body = new HashMap<>();
        body.put("error", ex.getMessage());

        return ResponseEntity.status(HttpStatus.CONFLICT).body(body); //409
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<Map<String,String>> handleUserNotFound(UserNotFoundException ex){
        Map<String,String> body = new HashMap<>();
        body.put("error", ex.getMessage());

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body); //404
    }

    /**
     * Returns 401 when login credentials are invalid.
     */
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<Map<String, String>> handleAuthentication(AuthenticationException ex) {
        Map<String, String> body = new HashMap<>();
        body.put("error", "Invalid email or password");

        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(body);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationErrors(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult()
                .getFieldErrors()
                .forEach(error ->
                        errors.put(error.getField(), error.getDefaultMessage()));

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(errors);
    }

    @ExceptionHandler(SpendingPeriodNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleSpendingPeriodNotFound(SpendingPeriodNotFoundException ex){
        Map<String, String> body = new HashMap<>();
        body.put("error", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND).body(body);
    }

    @ExceptionHandler(InvalidSpendingPeriodException.class)
    public ResponseEntity<Map<String, String>> handleInvalidSpendingPeriod(InvalidSpendingPeriodException ex){
        Map<String,String> body =new HashMap<>();
        body.put("error", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(SpendingPeriodOverlapException.class)
    public ResponseEntity<Map<String, String>> handleSpendingPeriodOverlap(SpendingPeriodOverlapException ex){
        Map<String, String> body = new HashMap<>();
        body.put("error", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }
    @ExceptionHandler(CategoryNotFoundException.class)
    public ResponseEntity<Map<String,String>> handleCategoryNotFound(CategoryNotFoundException ex){
        Map<String, String> body =new HashMap<>();
        body.put("error",ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }

    @ExceptionHandler(DuplicateCategoryException.class)
    public ResponseEntity<Map<String, String>> handleDuplicateCategory(DuplicateCategoryException ex){
        Map<String, String> body = new HashMap<>();
        body.put("error", ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(body);
    }

    @ExceptionHandler(CategoryInUseException.class)
    public ResponseEntity<Map<String, String>> handleCategoryInUse(
            CategoryInUseException ex) {

        Map<String, String> body = new HashMap<>();
        body.put("error", ex.getMessage());

        return ResponseEntity.status(HttpStatus.CONFLICT).body(body);
    }
}
