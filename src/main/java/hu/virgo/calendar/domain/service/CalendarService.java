package hu.virgo.calendar.domain.service;

import hu.virgo.calendar.domain.model.Event;
import hu.virgo.calendar.domain.model.TimeSlot;
import hu.virgo.calendar.domain.validation.CalendarValidationException;
import hu.virgo.calendar.domain.validation.EventValidator;
import hu.virgo.calendar.infrastructure.configuration.CalendarConfiguration;
import hu.virgo.calendar.infrastructure.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CalendarService {

    private final EventRepository eventRepository;
    private final CalendarConfiguration calendarConfiguration;
    private final EventValidator eventValidator;

    /**
     * Saving {@link Event}, validation takes place here!
     * @param event {@link Event}
     * @return The created {@link Event}
     */
    public Event save(Event event) {
        eventValidator.validate(event);
        Optional<Event> overlappingEvent = eventRepository.findByStartTimeLessThanAndEndTimeGreaterThan(event.endTime(), event.startTime());
        if (overlappingEvent.isPresent()) {
            throw new CalendarValidationException("Overlapping event present!");
        }

        return eventRepository.save(event);
    }

    /**
     * Find {@link Event} by a given date
     * @param offsetDateTime The given date
     * @return {@link Optional<Event>}, if event can be found.
     */
    public Optional<Event> findByDateTime(OffsetDateTime offsetDateTime) {
        return eventRepository.findByStartTimeLessThanAndEndTimeGreaterThan(offsetDateTime, offsetDateTime);
    }

    /**
     * Schedule of the week. The week will be calculated by the given date and time. Events will be sorted by their {@link Event#startTime()}
     * @param dateTime The given date and time to define the week.
     * @return List of {@link Event}s
     */
    public List<Event> scheduleOfWeek(OffsetDateTime dateTime) {
        OffsetDateTime startOfWeek = calendarConfiguration.startOfWeek(dateTime);
        OffsetDateTime endOfWeek = calendarConfiguration.endOfWeek(dateTime);
        return eventRepository.findByStartTimeGreaterThanEqualAndEndTimeLessThanEqual(startOfWeek, endOfWeek);
    }

    /**
     * Will find the available and bookable {@link TimeSlot}s of the given week. The week will be calculated by the given date and time.
     * @param dateTime The given date and time to define the week.
     * @return List of {@link TimeSlot}s
     */
    public List<TimeSlot> availableTimeSlotsOfWeek(OffsetDateTime dateTime) {
        OffsetDateTime startOfWeek = calendarConfiguration.startOfWeek(dateTime);
        OffsetDateTime endOfWeek = calendarConfiguration.startOfWeek(dateTime);
        OffsetTime endOfDay = calendarConfiguration.getEndOfDay();

        // get events for the week
        List<Event> events = scheduleOfWeek(dateTime);

        List<TimeSlot> timeSlots = new ArrayList<>();
        OffsetDateTime timeSlotStart = startOfWeek;

        // iterate until the end of the week
        while (!timeSlotStart.isAfter(endOfWeek)) {

            //filter same day events
            List<Event> sameDayEvents = sameDayEvents(events, timeSlotStart);
            //creat TimeSlots for the given day
            for (Event event : sameDayEvents) {
                if (event.startTime().isAfter(timeSlotStart)) {
                    timeSlots.add(new TimeSlot(timeSlotStart, event.startTime()));
                }
                timeSlotStart = event.endTime();

            }
            if (timeSlotStart.toOffsetTime().isBefore(endOfDay)) {
                OffsetDateTime timeSlotEnd = timeSlotStart
                        .withHour(endOfDay.getHour())
                        .withMinute(endOfDay.getMinute());
                timeSlots.add(new TimeSlot(timeSlotStart, timeSlotEnd));
            }

            //go for the next day
            timeSlotStart = startOfWeek.plusDays(1);
        }

        return timeSlots;
    }

    private List<Event> sameDayEvents(List<Event> events, OffsetDateTime dateTime) {
        return events.stream()
                .filter(e -> e.startTime().getDayOfWeek() == dateTime.getDayOfWeek())
                .sorted(Comparator.comparing(Event::startTime))
                .toList();
    }
}
