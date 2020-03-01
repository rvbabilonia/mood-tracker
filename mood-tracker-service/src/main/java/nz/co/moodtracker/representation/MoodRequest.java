package nz.co.moodtracker.representation;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.util.Objects;

/**
 * The mood request body.
 *
 * @author Rey Vincent Babilonia
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonDeserialize(builder = MoodRequest.Builder.class)
public class MoodRequest {

    @Min(1)
    @Max(5)
    private final int rating;
    private final String message;

    /**
     * Private constructor.
     *
     * @param builder the {@link Builder}
     */
    private MoodRequest(Builder builder) {
        this.rating = builder.category;
        this.message = builder.message;
    }

    /**
     * Returns the rating.
     *
     * @return the rating
     */
    public int getRating() {
        return rating;
    }

    /**
     * Returns the optional message.
     *
     * @return the optional message
     */
    public String getMessage() {
        return message;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        MoodRequest that = (MoodRequest) o;
        return rating == that.rating
                && Objects.equals(message, that.message);
    }

    @Override
    public int hashCode() {
        return Objects.hash(rating, message);
    }

    @Override
    public String toString() {
        return "MoodRequest{"
                + "rating=" + rating
                + ", message='" + message + '\''
                + '}';
    }

    /**
     * The builder.
     */
    @JsonPOJOBuilder
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Builder {

        private int category;
        private String message;

        /**
         * Sets the rating.
         *
         * @param category the rating
         * @return the {@link Builder}
         */
        public Builder withRating(int category) {
            this.category = category;
            return this;
        }

        /**
         * Sets the optional message.
         *
         * @param message the optional message
         * @return the {@link Builder}
         */
        public Builder withMessage(String message) {
            this.message = message;
            return this;
        }

        /**
         * Builds a {@link MoodRequest}.
         *
         * @return the {@link MoodRequest}
         */
        public MoodRequest build() {
            return new MoodRequest(this);
        }
    }
}
