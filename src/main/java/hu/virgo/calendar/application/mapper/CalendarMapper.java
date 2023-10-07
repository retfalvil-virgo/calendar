package hu.virgo.calendar.application.mapper;

import hu.virgo.calendar.application.model.Event;
import hu.virgo.calendar.application.model.TimeSlot;
import org.mapstruct.Mapper;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;

@Mapper(componentModel = "spring")
public interface CalendarMapper {

    Event map(hu.virgo.calendar.domain.model.Event domainEvent);

    hu.virgo.calendar.domain.model.Event map(Event apiEvent);

    List<Event> mapToEvents(List<hu.virgo.calendar.domain.model.Event> domainEvents);

    List<TimeSlot> mapToTimeSlots(List<hu.virgo.calendar.domain.model.TimeSlot> domainTimeSlots);

    TimeSlot map(hu.virgo.calendar.domain.model.TimeSlot domainTimeSlot);

    default OffsetDateTime toOffsetDateTime(LocalDate localDate) {
        return OffsetDateTime.of(localDate, LocalTime.MIN, ZoneOffset.UTC);
    }

}
