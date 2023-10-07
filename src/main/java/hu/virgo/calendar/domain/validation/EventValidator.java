package hu.virgo.calendar.domain.validation;

import hu.virgo.calendar.domain.model.Event;
import hu.virgo.calendar.domain.model.Calendar;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.temporal.ChronoUnit;

@Component
@RequiredArgsConstructor
public class EventValidator {

    private final Calendar calendar;

    public void validate(Event event) {

        long durationInMinutes = ChronoUnit.MINUTES.between(event.getStartTime(), event.getEndTime());
        if (durationInMinutes < calendar.getMinEventLength()
                || durationInMinutes > calendar.getMaxEventLength()) {
            raiseException("Event duration must be between %s and %s minutes".formatted(calendar.getMinEventLength(), calendar.getMaxEventLength()));
        }

        if (durationInMinutes % calendar.getTimeSlotSize() != 0) {
            raiseException("Event duration must be the multiple of %s minutes".formatted(calendar.getTimeSlotSize()));
        }

        if (!calendar.isDayOfWeekAvailable(event.getStartTime().getDayOfWeek())
                || !calendar.isDayOfWeekAvailable(event.getEndTime().getDayOfWeek())) {
            raiseException("Event cannot take place on unavailable days!");
        }

        if (event.getStartTime().toOffsetTime().isBefore(calendar.getStartOfDay())
                || event.getEndTime().toOffsetTime().isAfter(calendar.getEndOfDay())) {
            raiseException("Event must be take place between: %s and %s".formatted(calendar.getStartOfDay(), calendar.getEndOfDay()) );
        }
        if (!calendar.getEventStartMinutes().contains(event.getStartTime().getMinute())
                || event.getStartTime().getSecond() != 0
                || event.getStartTime().getNano() != 0) {
            raiseException("Event must start at :%s or :00 in every hour!".formatted(calendar.getTimeSlotSize()) );
        }
    }

    private void raiseException(String message) {
        throw new CalendarValidationException(message);
    }
}
