package nz.co.moodtracker.service;

import nz.co.moodtracker.domain.Mood;
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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;

/**
 * The integration test case for {@link MoodService}.
 *
 * @author Rey Vincent Babilonia
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
class MoodServiceIntegrationTest {

    @Autowired
    private MoodService moodService;
    @Autowired
    private MoodRepository moodRepository;

    @BeforeEach
    void setUp() {
        moodRepository.deleteAll();
    }

    @Test
    void saveAndFlush() {
        String clientId = UUID.randomUUID().toString();

        MoodRequest request = new MoodRequest.Builder()
                .withRating(Category.MEH.getRating())
                .withMessage("meh")
                .build();

        Mood expected = moodService.saveAndFlush(clientId, request);
        assertThat(expected)
                .extracting("clientId", "rating", "message")
                .containsExactly(clientId, 3, "meh");
    }

    @Test
    void saveAndFlushADuplicate() {
        String clientId = UUID.randomUUID().toString();

        MoodRequest request1 = new MoodRequest.Builder()
                .withRating(Category.MEH.getRating())
                .withMessage("meh")
                .build();

        Mood expected = moodService.saveAndFlush(clientId, request1);
        assertThat(expected)
                .extracting("clientId", "rating", "message")
                .containsExactly(clientId, 3, "meh");

        MoodRequest request2 = new MoodRequest.Builder()
                .withRating(Category.NORMAL.getRating())
                .withMessage("normal")
                .build();

        ResponseAlreadySubmittedException e = catchThrowableOfType(() -> moodService.saveAndFlush(clientId, request2),
                ResponseAlreadySubmittedException.class);
        assertThat(e.getMessage())
                .isEqualTo("Sorry, you have already submitted your response for today, try again tomorrow!");
    }

    @Test
    void getOverallTeamMoodIndicator() {
        String clientId = UUID.randomUUID().toString();

        MoodRequest request1 = new MoodRequest.Builder()
                .withRating(Category.HAPPY.getRating())
                .withMessage("nice")
                .build();

        Mood expected = moodService.saveAndFlush(clientId, request1);
        assertThat(expected)
                .extracting("clientId", "rating", "message")
                .containsExactly(clientId, 5, "nice");

        clientId = UUID.randomUUID().toString();

        MoodRequest request2 = new MoodRequest.Builder()
                .withRating(Category.NORMAL.getRating())
                .withMessage("normal")
                .build();

        expected = moodService.saveAndFlush(clientId, request2);
        assertThat(expected)
                .extracting("clientId", "rating", "message")
                .containsExactly(clientId, 4, "normal");

        clientId = UUID.randomUUID().toString();

        MoodRequest request3 = new MoodRequest.Builder()
                .withRating(Category.NORMAL.getRating())
                .withMessage("")
                .build();

        expected = moodService.saveAndFlush(clientId, request3);
        assertThat(expected)
                .extracting("clientId", "rating", "message")
                .containsExactly(clientId, 4, "");

        clientId = UUID.randomUUID().toString();

        MoodRequest request4 = new MoodRequest.Builder()
                .withRating(Category.STRESSED.getRating())
                .withMessage("tired")
                .build();

        expected = moodService.saveAndFlush(clientId, request4);
        assertThat(expected)
                .extracting("clientId", "rating", "message")
                .containsExactly(clientId, 1, "tired");

        OverallTeamMoodIndicatorResponse response = moodService.getOverallTeamMoodIndicator();

        assertThat(response.getRatings())
                .hasSize(3)
                .containsAllEntriesOf(Map.of(Category.HAPPY.getRating(), 1L,
                        Category.NORMAL.getRating(), 2L,
                        Category.STRESSED.getRating(), 1L));
        assertThat(response.getMessages())
                .hasSize(3)
                .containsExactly("nice", "normal", "tired");
        assertThat(response.getTotal()).isEqualTo(4L);
    }
}
