package nz.co.moodtracker.domain;

import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.OffsetDateTime;
import java.util.Objects;

/**
 * The domain model object for mood.
 *
 * @author Rey Vincent Babilonia
 */
@Entity
@Table(name = "mood")
public class Mood {

    private Long moodId;
    private String clientId;
    private int rating;
    private String message;
    private OffsetDateTime creationDate;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long getMoodId() {
        return moodId;
    }

    public void setMoodId(Long moodId) {
        this.moodId = moodId;
    }

    @Basic
    @Column(name = "client_id", nullable = false)
    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    @Basic
    @Column(name = "rating", nullable = false)
    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    @Basic
    @Column(name = "message")
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @CreationTimestamp
    public OffsetDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(OffsetDateTime creationDate) {
        this.creationDate = creationDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Mood mood = (Mood) o;
        return rating == mood.rating
                && Objects.equals(moodId, mood.moodId)
                && Objects.equals(clientId, mood.clientId)
                && Objects.equals(message, mood.message)
                && Objects.equals(creationDate, mood.creationDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(moodId, clientId, rating, message, creationDate);
    }

    @Override
    public String toString() {
        return "Mood{"
                + "moodId=" + moodId
                + ", clientId='" + clientId + '\''
                + ", rating=" + rating
                + ", message='" + message + '\''
                + ", creationDate=" + creationDate
                + '}';
    }
}
