package nz.co.moodtracker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * The mood tracker application.
 *
 * @author Rey Vincent Babilonia
 */
@SpringBootApplication
public class MoodTrackerApplication {

    /**
     * Main application.
     *
     * @param args the arguments
     */
    public static void main(String[] args) {
        SpringApplication.run(MoodTrackerApplication.class, args);
    }
}
