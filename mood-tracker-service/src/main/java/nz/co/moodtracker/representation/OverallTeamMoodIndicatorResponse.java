package nz.co.moodtracker.representation;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * The overall team mood indicator response body.
 *
 * @author Rey Vincent Babilonia
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonDeserialize(builder = OverallTeamMoodIndicatorResponse.Builder.class)
public class OverallTeamMoodIndicatorResponse {

    private final Map<Integer, Long> ratings;
    private final List<String> messages;
    private Long total;

    /**
     * Private constructor.
     *
     * @param builder the {@link Builder}
     */
    private OverallTeamMoodIndicatorResponse(Builder builder) {
        this.ratings = builder.ratings;
        this.messages = builder.messages;
        this.total = builder.total;
    }

    /**
     * Returns the {@link Map} of rating to its respective count.
     *
     * @return the {@link Map} of rating to its respective count
     */
    public Map<Integer, Long> getRatings() {
        return ratings;
    }

    /**
     * Returns the {@link List} of messages.
     *
     * @return the {@link List} of messages
     */
    public List<String> getMessages() {
        return messages;
    }

    /**
     * Returns the total number of respondents.
     *
     * @return the total number of respondents
     */
    public Long getTotal() {
        return total;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        OverallTeamMoodIndicatorResponse that = (OverallTeamMoodIndicatorResponse) o;
        return Objects.equals(ratings, that.ratings)
                && Objects.equals(messages, that.messages)
                && Objects.equals(total, that.total);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ratings, messages, total);
    }

    @Override
    public String toString() {
        return "OverallTeamMoodIndicatorResponse{"
                + "ratings=" + ratings
                + ", messages=" + messages
                + ", total=" + total
                + '}';
    }

    /**
     * The builder.
     */
    @JsonPOJOBuilder
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Builder {

        private Map<Integer, Long> ratings;
        private List<String> messages;
        private Long total;

        /**
         * Sets the {@link Map} of rating to its respective count.
         *
         * @param ratings the {@link Map} of rating to its respective count
         * @return the {@link Builder}
         */
        public Builder withRatings(Map<Integer, Long> ratings) {
            this.ratings = ratings;
            return this;
        }

        /**
         * Sets the {@link List} of messages.
         *
         * @param messages the {@link List} of messages
         * @return the {@link Builder}
         */
        public Builder withMessages(List<String> messages) {
            this.messages = messages;
            return this;
        }

        /**
         * Sets the total number of respondents.
         *
         * @param total the total number of respondents
         * @return the {@link Builder}
         */
        public Builder withTotal(Long total) {
            this.total = total;
            return this;
        }

        /**
         * Builds the {@link OverallTeamMoodIndicatorResponse}.
         *
         * @return the {@link OverallTeamMoodIndicatorResponse}
         */
        public OverallTeamMoodIndicatorResponse build() {
            return new OverallTeamMoodIndicatorResponse(this);
        }
    }
}
