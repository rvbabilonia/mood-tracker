package nz.co.moodtracker.service.impl;

import nz.co.moodtracker.domain.Mood;
import nz.co.moodtracker.domain.RatingCount;
import nz.co.moodtracker.exception.ResponseAlreadySubmittedException;
import nz.co.moodtracker.repository.MoodRepository;
import nz.co.moodtracker.representation.MoodRequest;
import nz.co.moodtracker.representation.OverallTeamMoodIndicatorResponse;
import nz.co.moodtracker.service.MoodService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * The implementation of {@link MoodService}.
 *
 * @author Rey Vincent Babilonia
 */
@Service
public class MoodServiceImpl
        implements MoodService {

    private static final String SUBMISSION_ERROR_MESSAGE = "Sorry, you have already submitted your response for today,"
            + " try again tomorrow!";
    private final MoodRepository moodRepository;

    /**
     * Default constructor.
     *
     * @param moodRepository the {@link MoodRepository}
     */
    public MoodServiceImpl(MoodRepository moodRepository) {
        this.moodRepository = moodRepository;
    }

    @Override
    public Mood saveAndFlush(final String clientId, MoodRequest request) {
        OffsetDateTime now = OffsetDateTime.now();
        OffsetDateTime startDateTime = now.withHour(0).withMinute(0).withSecond(0);
        OffsetDateTime endDateTime = now.withHour(23).withMinute(59).withSecond(0);

        Boolean exists = moodRepository.existsByClientIdAndCreationDateBetween(clientId, startDateTime, endDateTime);
        if (Boolean.TRUE.equals(exists)) {
            throw new ResponseAlreadySubmittedException(SUBMISSION_ERROR_MESSAGE);
        }

        Mood mood = new Mood();
        mood.setClientId(clientId);
        mood.setRating(request.getRating());
        mood.setMessage(request.getMessage());

        return moodRepository.saveAndFlush(mood);
    }

    @Override
    public OverallTeamMoodIndicatorResponse getOverallTeamMoodIndicator() {
        OffsetDateTime now = OffsetDateTime.now();
        OffsetDateTime startDateTime = now.withHour(0).withMinute(0).withSecond(0);
        OffsetDateTime endDateTime = now.withHour(23).withMinute(59).withSecond(0);

        List<RatingCount> ratingCounts = moodRepository.getRatingCounts(startDateTime, endDateTime);
        Map<Integer, Long> ratings = new TreeMap<>();
        AtomicLong total = new AtomicLong();
        if (!ratingCounts.isEmpty()) {
            ratingCounts.forEach(ratingCount -> {
                ratings.put(ratingCount.getRating(), ratingCount.getCount());
                total.addAndGet(ratingCount.getCount());
            });
        }

        List<Mood> moods = moodRepository.findAllByCreationDateBetween(startDateTime, endDateTime);
        List<String> messages = moods.stream()
                .filter(mood -> StringUtils.isNotBlank(mood.getMessage()))
                .map(Mood::getMessage)
                .collect(Collectors.toList());

        return new OverallTeamMoodIndicatorResponse.Builder()
                .withRatings(ratings)
                .withMessages(messages)
                .withTotal(total.get())
                .build();
    }
}
