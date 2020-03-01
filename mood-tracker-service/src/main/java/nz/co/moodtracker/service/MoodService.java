package nz.co.moodtracker.service;

import nz.co.moodtracker.domain.Mood;
import nz.co.moodtracker.representation.MoodRequest;
import nz.co.moodtracker.representation.OverallTeamMoodIndicatorResponse;

/**
 * The service for saving a {@link Mood} and {@link OverallTeamMoodIndicatorResponse}.
 *
 * @author Rey Vincent Babilonia
 */
public interface MoodService {

    /**
     * Saves and flushes the {@link Mood}.
     *
     * @param clientId the UUID of the client
     * @param mood     the {@link MoodRequest}
     * @return the {@link Mood}
     */
    Mood saveAndFlush(String clientId, MoodRequest mood);

    /**
     * Returns the {@link OverallTeamMoodIndicatorResponse} for the day after a successful submission.
     * TODO: pagination and creation date sorting can be implemented later on
     *
     * @return the {@link OverallTeamMoodIndicatorResponse} for the day
     */
    OverallTeamMoodIndicatorResponse getOverallTeamMoodIndicator();
}
