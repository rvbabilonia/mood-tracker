package nz.co.moodtracker.repository;

import nz.co.moodtracker.domain.Mood;
import nz.co.moodtracker.domain.RatingCount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.OffsetDateTime;
import java.util.List;

/**
 * The data access object or repository for mood.
 *
 * @author Rey Vincent Babilonia
 */
public interface MoodRepository
        extends JpaRepository<Mood, Long> {

    /**
     * Returns the {@link List} of {@link Mood}s for the given day.
     *
     * @param startDateTime the start {@link OffsetDateTime}
     * @param endDateTime   the end {@link OffsetDateTime}
     * @return the {@link List} of {@link Mood}s between the 2 {@link OffsetDateTime}s
     */
    List<Mood> findAllByCreationDateBetween(OffsetDateTime startDateTime, OffsetDateTime endDateTime);

    /**
     * Returns the {@link List} of {@link RatingCount}s for the given day.
     *
     * @param startDateTime the start {@link OffsetDateTime}
     * @param endDateTime   the end {@link OffsetDateTime}
     * @return the {@link List} of {@link RatingCount}s between the 2 {@link OffsetDateTime}s
     */
    @Query("SELECT new nz.co.moodtracker.domain.RatingCount(m.rating, COUNT(m))"
            + " FROM Mood m"
            + " WHERE m.creationDate BETWEEN ?1 AND ?2"
            + " GROUP BY m.rating"
            + " ORDER BY m.rating ASC")
    List<RatingCount> getRatingCounts(OffsetDateTime startDateTime, OffsetDateTime endDateTime);

    /**
     * Checks if a record exists for client UUID for the given day.
     *
     * @param clientId      the client UUID
     * @param startDateTime the start {@link OffsetDateTime}
     * @param endDateTime   the end {@link OffsetDateTime}
     * @return {@code true} if a record exists for client UUID for the given day; {@code false} otherwise
     */
    Boolean existsByClientIdAndCreationDateBetween(String clientId, OffsetDateTime startDateTime,
                                                   OffsetDateTime endDateTime);
}
