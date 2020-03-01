package nz.co.moodtracker.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import nz.co.moodtracker.domain.Mood;
import nz.co.moodtracker.enumeration.Category;
import nz.co.moodtracker.exception.ResponseAlreadySubmittedException;
import nz.co.moodtracker.representation.MoodRequest;
import nz.co.moodtracker.representation.OverallTeamMoodIndicatorResponse;
import nz.co.moodtracker.service.MoodService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import javax.servlet.http.Cookie;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * The unit test case for {@link MoodController}.
 *
 * @author Rey Vincent Babilonia
 */
@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = MoodController.class)
@ActiveProfiles("test")
class MoodControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private MoodService moodService;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void createMood() throws Exception {
        OverallTeamMoodIndicatorResponse response = new OverallTeamMoodIndicatorResponse.Builder()
                .withRatings(new TreeMap<>(Map.of(Category.STRESSED.getRating(), 1L, Category.NORMAL.getRating(), 2L,
                        Category.HAPPY.getRating(), 1L)))
                .withMessages(List.of("tired", "normal", "nice"))
                .withTotal(4L)
                .build();
        given(moodService.getOverallTeamMoodIndicator()).willReturn(response);

        String clientId = UUID.randomUUID().toString();
        MoodRequest request = new MoodRequest.Builder()
                .withRating(Category.STRESSED.getRating())
                .withMessage("tired")
                .build();

        given(moodService.saveAndFlush(clientId, request)).willReturn(mock(Mood.class));

        mockMvc.perform(post("/api/v1/moods")
                .cookie(new Cookie("clientId", clientId))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string("{\"ratings\":{\"1\":1,\"4\":2,\"5\":1},"
                        + "\"messages\":[\"tired\",\"normal\",\"nice\"],\"total\":4}"));
    }

    @Test
    void createDuplicateMood() throws Exception {
        String clientId = UUID.randomUUID().toString();
        MoodRequest request = new MoodRequest.Builder()
                .withRating(Category.STRESSED.getRating())
                .withMessage("tired")
                .build();

        given(moodService.saveAndFlush(clientId, request))
                .willThrow(new ResponseAlreadySubmittedException("Duplicate submission"));

        mockMvc.perform(post("/api/v1/moods")
                .cookie(new Cookie("clientId", clientId))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("{\"message\":\"Duplicate submission\"}"));
    }

    @Test
    void createMoodWithError() throws Exception {
        String clientId = UUID.randomUUID().toString();
        MoodRequest request = new MoodRequest.Builder()
                .withRating(Category.STRESSED.getRating())
                .withMessage("tired")
                .build();

        given(moodService.saveAndFlush(clientId, request))
                .willThrow(new RuntimeException("Other exception"));

        mockMvc.perform(post("/api/v1/moods")
                .cookie(new Cookie("clientId", clientId))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("{\"message\":\"Other exception\"}"));
    }

    @Test
    void createMoodWithoutClientId() throws Exception {
        MoodRequest request = new MoodRequest.Builder()
                .withRating(Category.STRESSED.getRating())
                .withMessage("tired")
                .build();

        mockMvc.perform(post("/api/v1/moods")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(cookie().exists("clientId"))
                .andExpect(cookie().maxAge("clientId", 86400))
                .andExpect(cookie().path("clientId", "/"));
    }

    @Test
    void createMoodWithoutRequestBody() throws Exception {
        String clientId = UUID.randomUUID().toString();

        mockMvc.perform(post("/api/v1/moods")
                .cookie(new Cookie("clientId", clientId))
                .contentType(MediaType.APPLICATION_JSON)
                .content(""))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("{\"message\":\"Required request body is missing: public"
                        + " org.springframework.http.ResponseEntity<?> nz.co.moodtracker.controller.MoodController"
                        + ".createMood(java.lang.String,nz.co.moodtracker.representation.MoodRequest)\"}"));
    }

    @Test
    void createMoodWithInvalidRating() throws Exception {
        String clientId = UUID.randomUUID().toString();
        MoodRequest request = new MoodRequest.Builder()
                .withRating(10)
                .withMessage("super happy")
                .build();

        mockMvc.perform(post("/api/v1/moods")
                .cookie(new Cookie("clientId", clientId))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("{\"rating\":\"must be less than or equal to 5\"}"));
    }

    @Test
    void createMoodWithoutRating() throws Exception {
        String clientId = UUID.randomUUID().toString();
        MoodRequest request = new MoodRequest.Builder()
                .withMessage("super happy")
                .build();

        mockMvc.perform(post("/api/v1/moods")
                .cookie(new Cookie("clientId", clientId))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("{\"rating\":\"must be greater than or equal to 1\"}"));
    }

    @Test
    void ping() throws Exception {
        mockMvc.perform(get("/api/v1/ping"))
                .andExpect(status().isOk())
                .andExpect(content().string("{\"ping\":\"null\"}"));
    }
}
