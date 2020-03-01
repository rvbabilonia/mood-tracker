package nz.co.moodtracker.repository;

import nz.co.moodtracker.domain.Mood;
import nz.co.moodtracker.domain.RatingCount;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * The integration test case for {@link MoodRepository}.
 *
 * @author Rey Vincent Babilonia
 */
@ExtendWith(SpringExtension.class)
@DataJpaTest
@ActiveProfiles("test")
class MoodRepositoryIntegrationTest {

    @Autowired
    private MoodRepository moodRepository;

    @Test
    void findAllByCreationDateBetween() {
        String clientId = UUID.randomUUID().toString();
        OffsetDateTime now = OffsetDateTime.now();
        OffsetDateTime startDateTime = now.withHour(0).withMinute(0).withSecond(0);
        OffsetDateTime endDateTime = now.withHour(23).withMinute(59).withSecond(0);

        Mood mood = new Mood();
        mood.setClientId(clientId);
        mood.setRating(2);
        mood.setMessage("grumpy");

        moodRepository.saveAndFlush(mood);

        List<Mood> expected = moodRepository.findAllByCreationDateBetween(startDateTime, endDateTime);
        assertThat(expected)
                .hasSize(1)
                .first()
                .extracting("clientId", "rating", "message")
                .containsExactly(clientId, 2, "grumpy");

        expected = moodRepository.findAllByCreationDateBetween(startDateTime.minusDays(1), endDateTime.minusDays(1));
        assertThat(expected).isEmpty();
    }

    @Test
    void getRatingCounts() {
        Mood mood1 = new Mood();
        mood1.setClientId(UUID.randomUUID().toString());
        mood1.setRating(5);
        mood1.setMessage("happy");

        moodRepository.saveAndFlush(mood1);

        Mood mood2 = new Mood();
        mood2.setClientId(UUID.randomUUID().toString());
        mood2.setRating(2);
        mood2.setMessage("grumpy");

        moodRepository.saveAndFlush(mood2);

        Mood mood3 = new Mood();
        mood3.setClientId(UUID.randomUUID().toString());
        mood3.setRating(2);
        mood3.setMessage("oh well");

        moodRepository.saveAndFlush(mood3);

        OffsetDateTime now = OffsetDateTime.now();
        OffsetDateTime startDateTime = now.withHour(0).withMinute(0).withSecond(0);
        OffsetDateTime endDateTime = now.withHour(23).withMinute(59).withSecond(0);

        List<RatingCount> expected = moodRepository.getRatingCounts(startDateTime, endDateTime);
        assertThat(expected).hasSize(2);
        expected.forEach(ratingCount -> {
            if (ratingCount.getRating() == 5) {
                assertThat(ratingCount.getCount()).isEqualTo(1L);
            } else if (ratingCount.getRating() == 2) {
                assertThat(ratingCount.getCount()).isEqualTo(2);
            } else {
                assertThat(ratingCount.getCount()).isEqualTo(0);
            }
        });
    }

    @Test
    void existsByClientIdAndCreationDateBetween() {
        String clientId = UUID.randomUUID().toString();
        OffsetDateTime now = OffsetDateTime.now();
        OffsetDateTime startDateTime = now.withHour(0).withMinute(0).withSecond(0);
        OffsetDateTime endDateTime = now.withHour(23).withMinute(59).withSecond(0);

        assertThat(moodRepository.existsByClientIdAndCreationDateBetween(clientId, startDateTime, endDateTime))
                .isFalse();

        Mood mood = new Mood();
        mood.setClientId(clientId);
        mood.setRating(3);
        mood.setMessage("meh");

        Mood expected = moodRepository.saveAndFlush(mood);

        assertThat(expected)
                .extracting("clientId", "rating", "message")
                .containsExactly(clientId, 3, "meh");

        assertThat(moodRepository.existsByClientIdAndCreationDateBetween(clientId, startDateTime, endDateTime))
                .isTrue();
    }
}
