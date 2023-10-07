package hu.virgo.calendar.application.contoller;

import hu.virgo.calendar.application.api.CalendarApi;
import hu.virgo.calendar.application.mapper.CalendarMapper;
import hu.virgo.calendar.application.model.Event;
import hu.virgo.calendar.application.model.TimeSlot;
import hu.virgo.calendar.domain.service.EventService;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class CalendarController implements CalendarApi {

    private final EventService eventService;
    private final CalendarMapper calendarMapper;

    @Override
    public ResponseEntity<Event> getEventByDateTime(OffsetDateTime dateTime) {
        return eventService.findByDateTime(dateTime)
                .map(e -> ResponseEntity.ok(calendarMapper.map(e)))
                .orElse(ResponseEntity.notFound().build());
    }

    @Override
    public ResponseEntity<Event> createEvent(Event event) {
        try {
            Event created = calendarMapper.map(eventService.save(calendarMapper.map(event)));
            return ResponseEntity.ok(created);
        } catch (ValidationException ex) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, ex.getMessage());
        }
    }

    @Override
    public ResponseEntity<List<TimeSlot>> availableTimeSlotsOfWeek(LocalDate date) {
        OffsetDateTime offsetDateTime = calendarMapper.toOffsetDateTime(date);
        List<TimeSlot> timeSlots = calendarMapper.mapToTimeSlots(eventService.availableTimeSlotsOfWeek(offsetDateTime));
        return ResponseEntity.ok(timeSlots);
    }

    @Override
    public ResponseEntity<List<Event>> scheduleOfWeek(LocalDate date) {
        OffsetDateTime offsetDateTime = calendarMapper.toOffsetDateTime(date);
        List<Event> events = calendarMapper.mapToEvents(eventService.scheduleOfWeek(offsetDateTime));
        return ResponseEntity.ok(events);
    }
}