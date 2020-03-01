package nz.co.moodtracker.controller;

import nz.co.moodtracker.exception.ResponseAlreadySubmittedException;
import nz.co.moodtracker.representation.MoodRequest;
import nz.co.moodtracker.service.MoodService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestCookieException;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * The mood REST controller.
 *
 * @author Rey Vincent Babilonia
 */
@RestController
@RequestMapping(value = "api/v1", produces = MediaType.APPLICATION_JSON_VALUE)
@CrossOrigin(origins = {"http://localhost:4200", "http://web:4200"}, allowedHeaders = "*")
public class MoodController {

    private static final int MAX_AGE = 24 * 60 * 60; // expires in 24 hours
    private final MoodService moodService;

    /**
     * Default constructor.
     *
     * @param moodService the {@link MoodService}
     */
    public MoodController(MoodService moodService) {
        this.moodService = moodService;
    }

    /**
     * Creates a mood and returns the overall team mood indicator if successful.
     *
     * @param cookieClientId the client UUID from the cookie
     * @param request        the {@link MoodRequest}
     * @return the {@link ResponseEntity}
     */
    @PostMapping(value = "/moods", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> createMood(@CookieValue(value = "clientId", required = false) final String cookieClientId,
                                        @Valid @RequestBody MoodRequest request) {
        String clientId = cookieClientId;
        HttpHeaders headers = new HttpHeaders();
        if (StringUtils.isBlank(clientId)) {
            clientId = UUID.randomUUID().toString();
            headers.add("Set-Cookie",
                    String.format("clientId=%s; Max-Age=%d; Path=/; Secure; HttpOnly", clientId, MAX_AGE));
        }

        moodService.saveAndFlush(clientId, request);

        return ResponseEntity.ok().headers(headers).body(moodService.getOverallTeamMoodIndicator());
    }

    @GetMapping("/ping")
    public ResponseEntity<?> ping(@RequestParam(value = "message", required = false) String message) {
        return ResponseEntity.ok("{\"ping\":\"" + message + "\"}");
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, String> handleValidationExceptions(MethodArgumentNotValidException e) {
        Map<String, String> errors = new HashMap<>();
        e.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return errors;
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MissingRequestCookieException.class)
    public String handleMissingRequestCookieException(MissingRequestCookieException e) {
        return "{\"message\":\"" + e.getMessage() + "\"}";
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ResponseAlreadySubmittedException.class)
    public String handleResponseAlreadySubmittedException(ResponseAlreadySubmittedException e) {
        return "{\"message\":\"" + e.getMessage() + "\"}";
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public String handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
        return "{\"message\":\"" + e.getMessage() + "\"}";
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public String handleAllExceptions(Exception e) {
        return "{\"message\":\"" + e.getMessage() + "\"}";
    }
}
