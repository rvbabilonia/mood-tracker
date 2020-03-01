package nz.co.moodtracker.exception;

/**
 * The exception thrown when the request body is missing.
 *
 * @author Rey Vincent Babilonia
 */
public class MissingRequestException
        extends RuntimeException {

    /**
     * Default constructor.
     */
    public MissingRequestException() {
        super();
    }

    /**
     * Constructor with an error message.
     *
     * @param message the error message
     */
    public MissingRequestException(String message) {
        super(message);
    }
}
