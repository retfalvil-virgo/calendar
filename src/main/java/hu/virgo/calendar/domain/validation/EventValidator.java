package hu.virgo.calendar.domain.validation;

import hu.virgo.calendar.domain.model.Event;
import hu.virgo.calendar.infrastructure.configuration.CalendarConfiguration;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.temporal.ChronoUnit;

@Component
@RequiredArgsConstructor
public class EventValidator {

    private final CalendarConfiguration calendarConfiguration;

    public void validate(Event event) {

        long durationInMinutes = ChronoUnit.MINUTES.between(event.startTime(), event.endTime());
        if (durationInMinutes < calendarConfiguration.getMinEventLength()
                || durationInMinutes > calendarConfiguration.getMaxEventLength()) {
            raiseException(STR. "Event duration must be between \{ calendarConfiguration.getMinEventLength() } and \{ calendarConfiguration.getMaxEventLength() } minutes" );
        }

        if (durationInMinutes % calendarConfiguration.getTimeSlotSize() != 0) {
            raiseException(STR. "Event duration must be the multiple of \{ calendarConfiguration.getTimeSlotSize() } minutes" );
        }

        if (calendarConfiguration.getUnavailableDays().contains(event.startTime().getDayOfWeek()) || calendarConfiguration.getUnavailableDays().contains(event.endTime().getDayOfWeek())) {
            raiseException(STR."Event cannot take place on unavailable days!");
        }

        if (event.startTime().toOffsetTime().isBefore(calendarConfiguration.eventStartLimit()) || event.endTime().toOffsetTime().isAfter(calendarConfiguration.eventEndLimit())) {
            raiseException(STR. "Event must be take place between: \{ calendarConfiguration.eventStartLimit() } and \{ calendarConfiguration.eventEndLimit() }" );
        }
        //FIXME, not timeslotsize but starting
        if (event.startTime().getMinute() % calendarConfiguration.getTimeSlotSize() != 0
                || event.startTime().getSecond() != 0
                || event.startTime().getNano() != 0) {
            raiseException(STR. "Event must start at :\{ calendarConfiguration.getTimeSlotSize() } or :00 in every hour!" );
        }
    }

    private void raiseException(String message) {
        throw new CalendarValidationException(message);
    }
}
