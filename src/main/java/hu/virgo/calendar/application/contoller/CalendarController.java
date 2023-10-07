package hu.virgo.calendar.application.contoller;

import hu.virgo.calendar.application.api.CalendarApi;
import hu.virgo.calendar.application.mapper.CalendarMapper;
import hu.virgo.calendar.application.model.Event;
import hu.virgo.calendar.application.model.TimeSlot;
import hu.virgo.calendar.domain.service.CalendarService;
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

    private final CalendarService calendarService;
    private final CalendarMapper calendarMapper;

    @Override
    public ResponseEntity<Event> calendarEventsGet(OffsetDateTime dateTime) {
        return calendarService.findByDateTime(dateTime)
                .map(e -> ResponseEntity.ok(calendarMapper.map(e)))
                .orElse(ResponseEntity.notFound().build());
    }

    @Override
    public ResponseEntity<Event> calendarEventsPost(Event event) {
        try {
            Event created = calendarMapper.map(calendarService.save(calendarMapper.map(event)));
            return ResponseEntity.ok(created);
        } catch (ValidationException ex) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, ex.getMessage());
        }
    }

    @Override
    public ResponseEntity<List<TimeSlot>> calendarScheduleAvailableDateGet(LocalDate date) {
        OffsetDateTime offsetDateTime = calendarMapper.toOffsetDateTime(date);
        List<TimeSlot> timeSlots = calendarMapper.mapToTimeSlots(calendarService.availableTimeSlotsOfWeek(offsetDateTime));
        return ResponseEntity.ok(timeSlots);
    }

    @Override
    public ResponseEntity<List<Event>> calendarScheduleDateGet(LocalDate date) {
        OffsetDateTime offsetDateTime = calendarMapper.toOffsetDateTime(date);
        List<Event> events = calendarMapper.mapToEvents(calendarService.scheduleOfWeek(offsetDateTime));
        return ResponseEntity.ok(events);
    }
}