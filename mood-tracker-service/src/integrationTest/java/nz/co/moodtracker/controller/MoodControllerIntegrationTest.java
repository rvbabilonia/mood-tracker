package nz.co.moodtracker.controller;

import nz.co.moodtracker.enumeration.Category;
import nz.co.moodtracker.exception.ResponseAlreadySubmittedException;
import nz.co.moodtracker.repository.MoodRepository;
import nz.co.moodtracker.representation.MoodRequest;
import nz.co.moodtracker.representation.OverallTeamMoodIndicatorResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;

/**
 * The integration test case for {@link MoodController}.
 *
 * @author Rey Vincent Babilonia
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
class MoodControllerIntegrationTest {

    @Autowired
    private MoodController moodController;
    @Autowired
    private MoodRepository moodRepository;

    @BeforeEach
    void setUp() {
        moodRepository.deleteAll();
    }

    @Test
    void createMood() {
        String clientId = UUID.randomUUID().toString();
        MoodRequest request = new MoodRequest.Builder()
                .withRating(Category.STRESSED.getRating())
                .withMessage("tired")
                .build();

        ResponseEntity<?> responseEntity = moodController.createMood(clientId, request);

        assertThat(responseEntity)
                .extracting("statusCodeValue", "body")
                .containsExactly(200, new OverallTeamMoodIndicatorResponse.Builder()
                        .withRatings(Map.of(1, 1L))
                        .withMessages(List.of("tired"))
                        .withTotal(1L)
                        .build());
    }

    @Test
    void createDuplicateMood() {
        String clientId = UUID.randomUUID().toString();
        MoodRequest request = new MoodRequest.Builder()
                .withRating(Category.STRESSED.getRating())
                .withMessage("tired")
                .build();

        ResponseEntity<?> responseEntity = moodController.createMood(clientId, request);

        assertThat(responseEntity)
                .extracting("statusCodeValue", "body")
                .containsExactly(200, new OverallTeamMoodIndicatorResponse.Builder()
                        .withRatings(Map.of(1, 1L))
                        .withMessages(List.of("tired"))
                        .withTotal(1L)
                        .build());

        ResponseAlreadySubmittedException e = catchThrowableOfType(() -> moodController.createMood(clientId, request),
                ResponseAlreadySubmittedException.class);

        assertThat(e.getMessage())
                .isEqualTo("Sorry, you have already submitted your response for today, try again tomorrow!");
    }

    @Test
    void ping() {
        ResponseEntity<?> responseEntity = moodController.ping("message");

        assertThat(responseEntity)
                .extracting("statusCodeValue", "body")
                .containsExactly(200, "{\"ping\":\"message\"}");
    }
}
