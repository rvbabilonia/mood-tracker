package nz.co.moodtracker.domain;

import java.util.Objects;

/**
 * The aggregation of rating to count.
 *
 * @author Rey Vincent Babilonia
 */
public class RatingCount {

    private final Integer rating;
    private final Long count;

    /**
     * Default constructor.
     *
     * @param rating the rating
     * @param count  the count
     */
    public RatingCount(Integer rating, Long count) {
        this.rating = rating;
        this.count = count;
    }

    /**
     * Returns the rating.
     *
     * @return the rating
     */
    public Integer getRating() {
        return rating;
    }

    /**
     * Returns the count.
     *
     * @return the count
     */
    public Long getCount() {
        return count;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        RatingCount that = (RatingCount) o;
        return Objects.equals(rating, that.rating)
                && Objects.equals(count, that.count);
    }

    @Override
    public int hashCode() {
        return Objects.hash(rating, count);
    }

    @Override
    public String toString() {
        return "RatingCount{"
                + "rating=" + rating
                + ", count=" + count
                + '}';
    }
}
