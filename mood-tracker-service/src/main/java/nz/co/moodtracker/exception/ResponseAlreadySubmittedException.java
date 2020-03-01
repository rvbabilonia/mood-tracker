package nz.co.moodtracker.exception;

/**
 * The exception thrown when a client tries to submit another rating on the same day.
 *
 * @author Rey Vincent Babilonia
 */
public class ResponseAlreadySubmittedException
        extends RuntimeException {

    /**
     * Default constructor.
     */
    public ResponseAlreadySubmittedException() {
        super();
    }

    /**
     * Constructor with an error message.
     *
     * @param message the error message
     */
    public ResponseAlreadySubmittedException(String message) {
        super(message);
    }
}
