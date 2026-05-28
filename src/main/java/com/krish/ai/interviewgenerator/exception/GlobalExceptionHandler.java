package com.krish.ai.interviewgenerator.exception;

import com.krish.ai.interviewgenerator.dto.response.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleValidationException(
            MethodArgumentNotValidException ex) {
        log.warn("Validation failed for request: {}", ex.getMessage());
        Map<String, String> errors = new HashMap<>();
        for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            errors.put(fieldError.getField(), fieldError.getDefaultMessage());
        }

        ApiResponse<Map<String, String>> response = ApiResponse.<Map<String, String>>builder()
                .success(false)
                .message("Validation failed")
                .data(errors)
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<Void>> handleUnreadableMessage(HttpMessageNotReadableException ex) {
        log.warn("Invalid request payload: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.failure("Invalid request payload"));
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ApiResponse<Void>> handleIllegalState(IllegalStateException ex) {
        log.warn("Service unavailable: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(ApiResponse.failure(ex.getMessage()));
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<ApiResponse<Void>> handleNotFound(NoSuchElementException ex) {
        log.warn("Resource not found: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.failure(ex.getMessage()));
    }

    @ExceptionHandler(HttpStatusCodeException.class)
    public ResponseEntity<ApiResponse<Void>> handleHttpStatusCodeException(HttpStatusCodeException ex) {
        if (ex.getStatusCode().value() == 429 || containsRateLimitOrQuotaSignals(ex.getResponseBodyAsString())) {
            log.warn("AI provider rate-limit/quota response: status={}, body={}",
                    ex.getStatusCode().value(), ex.getResponseBodyAsString());
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                    .body(ApiResponse.failure("AI rate limit or quota exceeded. Please retry later or reduce request volume."));
        }

        log.error("AI provider HTTP error: status={}, body={}",
                ex.getStatusCode().value(), ex.getResponseBodyAsString());
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                .body(ApiResponse.failure("AI provider request failed"));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleGenericException(Exception ex) {
        String diagnosticText = collectExceptionText(ex);

        if (containsRateLimitOrQuotaSignals(diagnosticText)) {
            log.warn("AI rate-limit/quota error detected from exception chain: {}", diagnosticText);
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                    .body(ApiResponse.failure("AI rate limit or quota exceeded. Please retry later or reduce request volume."));
        }
        if (containsModelUnavailableSignals(diagnosticText)) {
            log.warn("AI model unavailable error detected from exception chain: {}", diagnosticText);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.failure("Configured AI model is unavailable. Please use a currently supported Gemini model."));
        }

        log.error("Unhandled server exception", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.failure("Unexpected server error"));
    }

    private String collectExceptionText(Throwable throwable) {
        StringBuilder builder = new StringBuilder();
        Throwable current = throwable;
        int depth = 0;
        while (current != null && depth < 10) {
            if (current.getClass() != null) {
                builder.append(current.getClass().getName()).append(' ');
            }
            if (current.getMessage() != null) {
                builder.append(current.getMessage()).append(' ');
            }
            current = current.getCause();
            depth++;
        }
        return builder.toString();
    }

    private boolean containsRateLimitOrQuotaSignals(String text) {
        if (text == null || text.isBlank()) {
            return false;
        }

        String normalized = text.toLowerCase();
        return normalized.contains("rate limit")
                || normalized.contains("too many requests")
                || normalized.contains("insufficient_quota")
                || normalized.contains("quota exceeded")
                || normalized.contains("429");
    }

    private boolean containsModelUnavailableSignals(String text) {
        if (text == null || text.isBlank()) {
            return false;
        }

        String normalized = text.toLowerCase();
        return normalized.contains("is not found for api version")
                || normalized.contains("is not supported for generatecontent")
                || normalized.contains("call listmodels to see the list of available models");
    }
}
