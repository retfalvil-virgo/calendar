package hu.virgo.calendar.domain.model;

import java.time.OffsetDateTime;

public record TimeSlot(OffsetDateTime start, OffsetDateTime end) {
}
