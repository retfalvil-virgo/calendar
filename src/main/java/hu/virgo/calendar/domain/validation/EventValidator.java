package hu.virgo.calendar.domain.validation;

import hu.virgo.calendar.domain.model.Event;
import hu.virgo.calendar.domain.model.CalendarProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.temporal.ChronoUnit;

@Component
@RequiredArgsConstructor
public class EventValidator {

    private final CalendarProperties calendarProperties;

    public void validate(Event event) {

        long durationInMinutes = ChronoUnit.MINUTES.between(event.getStartTime(), event.getEndTime());
        if (durationInMinutes < calendarProperties.getEventMinLength()
                || durationInMinutes > calendarProperties.getEventMaxLength()) {
            raiseException("Event duration must be between %s and %s minutes".formatted(calendarProperties.getEventMinLength(), calendarProperties.getEventMaxLength()));
        }

        if (durationInMinutes % calendarProperties.getTimeSlotSize() != 0) {
            raiseException("Event duration must be the multiple of %s minutes".formatted(calendarProperties.getTimeSlotSize()));
        }

        if (!calendarProperties.isDayOfWeekAvailable(event.getStartTime().getDayOfWeek())
                || !calendarProperties.isDayOfWeekAvailable(event.getEndTime().getDayOfWeek())) {
            raiseException("Event cannot take place on unavailable days!");
        }

        if (event.getStartTime().toOffsetTime().isBefore(calendarProperties.getEventStartTime())
                || event.getEndTime().toOffsetTime().isAfter(calendarProperties.getEventEndTime())) {
            raiseException("Event must be take place between: %s and %s".formatted(calendarProperties.getEventStartTime(), calendarProperties.getEventEndTime()) );
        }
        if (!calendarProperties.getEventStartMinutes().contains(event.getStartTime().getMinute())
                || event.getStartTime().getSecond() != 0
                || event.getStartTime().getNano() != 0) {
            raiseException("Event must start at :%s or :00 in every hour!".formatted(calendarProperties.getTimeSlotSize()) );
        }
    }

    private void raiseException(String message) {
        throw new CalendarValidationException(message);
    }
}
