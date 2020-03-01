package nz.co.moodtracker.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.assertion.DetailedCookieAssertion;
import io.restassured.http.Cookie;
import io.restassured.matcher.DetailedCookieMatcher;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import nz.co.moodtracker.enumeration.Category;
import nz.co.moodtracker.representation.MoodRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.context.WebApplicationContext;

import java.util.UUID;

import static io.restassured.module.mockmvc.RestAssuredMockMvc.given;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

/**
 * The end-to-end test case for {@link MoodController}.
 *
 * @author Rey Vincent Babilonia
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class MoodControllerE2ETest {

    @Autowired
    private WebApplicationContext webApplicationContext;
    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void beforeEach() {
        RestAssuredMockMvc.webAppContextSetup(webApplicationContext);
    }

    @Test
    void createMood() throws JsonProcessingException {
        String clientId = UUID.randomUUID().toString();
        Cookie cookie = new Cookie.Builder("clientId", clientId)
                .setMaxAge(86400)
                .setPath("/")
                .setSecured(true)
                .setHttpOnly(true)
                .build();
        MoodRequest request = new MoodRequest.Builder()
                .withRating(Category.STRESSED.getRating())
                .withMessage("tired")
                .build();

        given()
                .cookie(cookie)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(objectMapper.writeValueAsString(request))
                .when()
                .post("/api/v1/moods")
                .then()
                .statusCode(200)
                .body(containsString("ratings"))
                .body(containsString("messages"))
                .body(containsString("total"));
    }

    @Test
    void createDuplicateMood() throws JsonProcessingException {
        String clientId = UUID.randomUUID().toString();
        Cookie cookie = new Cookie.Builder("clientId", clientId)
                .setMaxAge(86400)
                .setPath("/")
                .setSecured(true)
                .setHttpOnly(true)
                .build();
        MoodRequest request = new MoodRequest.Builder()
                .withRating(Category.STRESSED.getRating())
                .withMessage("tired")
                .build();

        given()
                .cookie(cookie)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(objectMapper.writeValueAsString(request))
                .when()
                .post("/api/v1/moods")
                .then()
                .statusCode(200)
                .body(containsString("ratings"))
                .body(containsString("messages"))
                .body(containsString("total"));

        given()
                .cookie(cookie)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(objectMapper.writeValueAsString(request))
                .when()
                .post("/api/v1/moods")
                .then()
                .statusCode(400)
                .body(containsString("{\"message\":\"Sorry, you have already submitted your response for today,"
                        + " try again tomorrow!\"}"));
    }

    @Test
    void createMoodWithoutClientId() throws JsonProcessingException {
        MoodRequest request = new MoodRequest.Builder()
                .withRating(Category.STRESSED.getRating())
                .withMessage("tired")
                .build();

        given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(objectMapper.writeValueAsString(request))
                .when()
                .post("/api/v1/moods")
                .then()
                .statusCode(200)
                .cookie("clientId") // verify that a cookie was created
                .body(containsString("ratings"))
                .body(containsString("messages"))
                .body(containsString("total"));
    }

    @Test
    void createMoodWithoutRequestBody() {
        String clientId = UUID.randomUUID().toString();
        Cookie cookie = new Cookie.Builder("clientId", clientId)
                .setMaxAge(86400)
                .setPath("/")
                .setSecured(true)
                .setHttpOnly(true)
                .build();

        given()
                .cookie(cookie)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/api/v1/moods")
                .then()
                .statusCode(400)
                .body(containsString("{\"message\":\"Required request body is missing: public"
                        + " org.springframework.http.ResponseEntity<?> nz.co.moodtracker.controller.MoodController"
                        + ".createMood(java.lang.String,nz.co.moodtracker.representation.MoodRequest)\"}"));
    }

    @Test
    void createMoodWithInvalidRating() throws JsonProcessingException {
        String clientId = UUID.randomUUID().toString();
        Cookie cookie = new Cookie.Builder("clientId", clientId)
                .setMaxAge(86400)
                .setPath("/")
                .setSecured(true)
                .setHttpOnly(true)
                .build();
        MoodRequest request = new MoodRequest.Builder()
                .withRating(10)
                .withMessage("tired")
                .build();

        given()
                .cookie(cookie)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(objectMapper.writeValueAsString(request))
                .when()
                .post("/api/v1/moods")
                .then()
                .statusCode(400)
                .body(containsString("{\"rating\":\"must be less than or equal to 5\"}"));
    }

    @Test
    void createMoodWithoutRating() throws JsonProcessingException {
        String clientId = UUID.randomUUID().toString();
        Cookie cookie = new Cookie.Builder("clientId", clientId)
                .setMaxAge(86400)
                .setPath("/")
                .setSecured(true)
                .setHttpOnly(true)
                .build();
        MoodRequest request = new MoodRequest.Builder()
                .withMessage("tired")
                .build();

        given()
                .cookie(cookie)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(objectMapper.writeValueAsString(request))
                .when()
                .post("/api/v1/moods")
                .then()
                .statusCode(400)
                .body(containsString("{\"rating\":\"must be greater than or equal to 1\"}"));
    }

    @Test
    void ping() {
        given()
                .when()
                .get("/api/v1/ping?message=hello")
                .then()
                .log().ifValidationFails()
                .statusCode(200)
                .body(is(equalTo("{\"ping\":\"hello\"}")));
    }
}
