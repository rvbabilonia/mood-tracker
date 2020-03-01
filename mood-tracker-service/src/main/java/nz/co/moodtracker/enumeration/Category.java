package nz.co.moodtracker.enumeration;

/**
 * The enumeration of categories.
 *
 * @author Rey Vincent Babilonia
 */
public enum Category {

    /**
     * Stressed out – not a happy camper.
     */
    STRESSED(1),
    /**
     * Grumpy.
     */
    GRUMPY(2),
    /**
     * A bit "meh".
     */
    MEH(3),
    /**
     * Just normal really.
     */
    NORMAL(4),
    /**
     * Happy.
     */
    HAPPY(5);

    private final int rating;

    /**
     * Default constructor.
     *
     * @param rating the rating
     */
    Category(int rating) {
        this.rating = rating;
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
     * Returns the {@link Category} matching the rating.
     *
     * @param rating the rating
     * @return the {@link Category}
     */
    public static Category valueOf(int rating) {
        for (Category category : values()) {
            if (category.rating == rating) {
                return category;
            }
        }

        throw new IllegalArgumentException("Unknown rating [" + rating + "]");
    }
}
