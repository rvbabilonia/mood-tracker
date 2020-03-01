package nz.co.moodtracker.service;

import nz.co.moodtracker.domain.Mood;
import nz.co.moodtracker.domain.RatingCount;
import nz.co.moodtracker.enumeration.Category;
import nz.co.moodtracker.exception.ResponseAlreadySubmittedException;
import nz.co.moodtracker.repository.MoodRepository;
import nz.co.moodtracker.representation.MoodRequest;
import nz.co.moodtracker.representation.OverallTeamMoodIndicatorResponse;
import nz.co.moodtracker.service.impl.MoodServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * The unit test case for {@link MoodService}.
 *
 * @author Rey Vincent Babilonia
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class MoodServiceTest {

    @Mock
    private MoodRepository moodRepository;
    @InjectMocks
    private MoodServiceImpl moodService;

    @Test
    void saveAndFlush() {
        String clientId = UUID.randomUUID().toString();
        OffsetDateTime now = OffsetDateTime.now();

        Mood mood = mock(Mood.class);
        when(mood.getMoodId()).thenReturn(1L);
        when(mood.getClientId()).thenReturn(clientId);
        when(mood.getRating()).thenReturn(2);
        when(mood.getMessage()).thenReturn("grumpy");
        when(mood.getCreationDate()).thenReturn(now);
        when(moodRepository.saveAndFlush(any(Mood.class))).thenReturn(mood);

        MoodRequest request = new MoodRequest.Builder()
                .withRating(Category.GRUMPY.getRating())
                .withMessage("grumpy")
                .build();

        Mood expected = moodService.saveAndFlush(clientId, request);
        assertThat(expected)
                .extracting("clientId", "rating", "message")
                .containsExactly(clientId, 2, "grumpy");
    }

    @Test
    void saveAndFlushADuplicate() {
        String clientId = UUID.randomUUID().toString();
        OffsetDateTime now = OffsetDateTime.now();

        Mood mood = mock(Mood.class);
        when(mood.getMoodId()).thenReturn(1L);
        when(mood.getClientId()).thenReturn(clientId);
        when(mood.getRating()).thenReturn(3);
        when(mood.getMessage()).thenReturn("meh");
        when(mood.getCreationDate()).thenReturn(now);
        when(moodRepository.saveAndFlush(any(Mood.class))).thenReturn(mood);

        MoodRequest request1 = new MoodRequest.Builder()
                .withRating(Category.MEH.getRating())
                .withMessage("meh")
                .build();

        Mood expected = moodService.saveAndFlush(clientId, request1);
        assertThat(expected)
                .extracting("clientId", "rating", "message")
                .containsExactly(clientId, 3, "meh");

        when(moodRepository.existsByClientIdAndCreationDateBetween(anyString(), any(OffsetDateTime.class),
                any(OffsetDateTime.class)))
                .thenThrow(new ResponseAlreadySubmittedException("Sorry, you have already submitted your response"));

        MoodRequest request2 = new MoodRequest.Builder()
                .withRating(Category.NORMAL.getRating())
                .withMessage("normal")
                .build();

        ResponseAlreadySubmittedException e = catchThrowableOfType(() -> moodService.saveAndFlush(clientId, request2),
                ResponseAlreadySubmittedException.class);
        assertThat(e.getMessage())
                .isEqualTo("Sorry, you have already submitted your response");
    }

    @Test
    void getOverallTeamMoodIndicator() {
        String clientId = UUID.randomUUID().toString();
        OffsetDateTime now = OffsetDateTime.now();

        Mood mood1 = mock(Mood.class);
        when(mood1.getMoodId()).thenReturn(1L);
        when(mood1.getClientId()).thenReturn(clientId);
        when(mood1.getRating()).thenReturn(5);
        when(mood1.getMessage()).thenReturn("nice");
        when(mood1.getCreationDate()).thenReturn(now);
        when(moodRepository.saveAndFlush(any(Mood.class))).thenReturn(mood1);

        MoodRequest request1 = new MoodRequest.Builder()
                .withRating(Category.HAPPY.getRating())
                .withMessage("nice")
                .build();

        Mood expected = moodService.saveAndFlush(clientId, request1);
        assertThat(expected)
                .extracting("clientId", "rating", "message")
                .containsExactly(clientId, 5, "nice");

        clientId = UUID.randomUUID().toString();
        now = OffsetDateTime.now();

        Mood mood2 = mock(Mood.class);
        when(mood2.getMoodId()).thenReturn(1L);
        when(mood2.getClientId()).thenReturn(clientId);
        when(mood2.getRating()).thenReturn(4);
        when(mood2.getMessage()).thenReturn("normal");
        when(mood2.getCreationDate()).thenReturn(now);
        when(moodRepository.saveAndFlush(any(Mood.class))).thenReturn(mood2);

        MoodRequest request2 = new MoodRequest.Builder()
                .withRating(Category.NORMAL.getRating())
                .withMessage("normal")
                .build();

        expected = moodService.saveAndFlush(clientId, request2);
        assertThat(expected)
                .extracting("clientId", "rating", "message")
                .containsExactly(clientId, 4, "normal");

        clientId = UUID.randomUUID().toString();
        now = OffsetDateTime.now();

        Mood mood3 = mock(Mood.class);
        when(mood3.getMoodId()).thenReturn(1L);
        when(mood3.getClientId()).thenReturn(clientId);
        when(mood3.getRating()).thenReturn(4);
        when(mood3.getMessage()).thenReturn("");
        when(mood3.getCreationDate()).thenReturn(now);
        when(moodRepository.saveAndFlush(any(Mood.class))).thenReturn(mood3);

        MoodRequest request3 = new MoodRequest.Builder()
                .withRating(Category.NORMAL.getRating())
                .withMessage("")
                .build();

        expected = moodService.saveAndFlush(clientId, request3);
        assertThat(expected)
                .extracting("clientId", "rating", "message")
                .containsExactly(clientId, 4, "");

        clientId = UUID.randomUUID().toString();
        now = OffsetDateTime.now();

        Mood mood4 = mock(Mood.class);
        when(mood4.getMoodId()).thenReturn(1L);
        when(mood4.getClientId()).thenReturn(clientId);
        when(mood4.getRating()).thenReturn(1);
        when(mood4.getMessage()).thenReturn("tired");
        when(mood4.getCreationDate()).thenReturn(now);
        when(moodRepository.saveAndFlush(any(Mood.class))).thenReturn(mood4);

        MoodRequest request4 = new MoodRequest.Builder()
                .withRating(Category.STRESSED.getRating())
                .withMessage("tired")
                .build();

        expected = moodService.saveAndFlush(clientId, request4);
        assertThat(expected)
                .extracting("clientId", "rating", "message")
                .containsExactly(clientId, 1, "tired");

        when(moodRepository.getRatingCounts(any(OffsetDateTime.class), any(OffsetDateTime.class)))
                .thenReturn(List.of(new RatingCount(5, 1L), new RatingCount(4, 2L), new RatingCount(1, 1L)));

        when(moodRepository.findAllByCreationDateBetween(any(OffsetDateTime.class), any(OffsetDateTime.class)))
                .thenReturn(List.of(mood1, mood2, mood3, mood4));

        OverallTeamMoodIndicatorResponse response = moodService.getOverallTeamMoodIndicator();

        assertThat(response.getRatings())
                .hasSize(3)
                .containsAllEntriesOf(Map.of(Category.HAPPY.getRating(), 1L, Category.NORMAL.getRating(), 2L,
                        Category.STRESSED.getRating(), 1L));
        assertThat(response.getMessages())
                .hasSize(3)
                .containsExactly("nice", "normal", "tired");
        assertThat(response.getTotal()).isEqualTo(4L);
    }
}
