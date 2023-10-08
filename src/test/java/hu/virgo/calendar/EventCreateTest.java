package hu.virgo.calendar;

import com.fasterxml.jackson.databind.ObjectMapper;
import hu.virgo.calendar.application.model.Event;
import hu.virgo.calendar.domain.model.CalendarProperties;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class EventCreateTest {

    private static final String CREATE_EVENT_URL = "/calendar/events/";

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private CalendarProperties calendarProperties;

    @Test
    void testEventPost_invalidEventDuration() throws Exception {
        Event event = validEvent();
        event.endTime(event.getStartTime().plusHours(calendarProperties.getEventMaxLength()));

        performPost(event)
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.detail")
                        .value("Event duration must be between 30 and 180 minutes"));
    }

    @Test
    void testEventPost_eventDurationIsNotMultiple() throws Exception {
        Event event = validEvent();
        event.endTime(event.getStartTime().plusMinutes(42));

        performPost(event)
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.detail")
                        .value("Event duration must be the multiple of 30 minutes"));
    }

    @Test
    void testEventPost_eventOutsideOfBookingInterval() throws Exception {
        Event event = validEvent();
        event.setEndTime(event.getStartTime());
        event.setStartTime(event.getStartTime().minusHours(1));

        performPost(event)
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.detail")
                        .value("Event must be take place between: 09:00Z and 17:00Z"));
    }

    @Test
    @Transactional
    void testEventPost_overlappingEvents() throws Exception {
        Event event = validEvent();
        //store valid event
        performPost(event).andExpect(status().isOk());

        //overlapping event
        event.setStartTime(event.getEndTime().minusMinutes(30));
        event.setEndTime(event.getStartTime().plusHours(2));

        performPost(event)
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.detail")
                        .value("Overlapping event present!"));
    }

    @Test
    void testEventPost_invalidEventStartTime() throws Exception {
        Event event = validEvent();
        event.setStartTime(event.getStartTime().plusMinutes(10));
        event.setEndTime(event.getEndTime().plusMinutes(10));

        performPost(event)
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.detail")
                        .value("Event must start at :30 or :00 in every hour!"));
    }

    @Test
    @Transactional
    void testEventPost_bookTheWholeDay() throws Exception {
        Event event = validEvent();
        //store valid event
        performPost(event).andExpect(status().isOk());

        event.setStartTime(event.getEndTime());
        event.setEndTime(event.getStartTime().plusHours(2));
        performPost(event).andExpect(status().isOk());

        event.setStartTime(event.getEndTime());
        event.setEndTime(event.getStartTime().plusHours(2));
        performPost(event).andExpect(status().isOk());

        event.setStartTime(event.getEndTime());
        event.setEndTime(event.getStartTime().plusHours(3));
        performPost(event).andExpect(status().isOk());
    }

    private ResultActions performPost(Event event) throws Exception {
        return mockMvc.perform(post(CREATE_EVENT_URL)
                .content(asJsonString(event))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));
    }

    private Event validEvent() {
        OffsetDateTime offsetDateTime = OffsetDateTime.of(2023, 10, 5, 9, 0, 0, 0, ZoneOffset.UTC);
        Event event = new Event();
        event.setOrganizer("test_organizer");
        event.startTime(offsetDateTime);
        event.endTime(offsetDateTime.plusHours(1));
        return event;
    }

    public String asJsonString(final Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
