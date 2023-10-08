package hu.virgo.calendar.domain.model;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.DayOfWeek;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.temporal.TemporalAdjusters;
import java.util.Set;

@Data
@ConfigurationProperties("hu.virgo.calendar")
public class CalendarProperties {

    private static final int WEEK_REMAINDER = 6;

    private OffsetTime eventStartTime;
    private OffsetTime eventEndTime;
    private int eventMinLength;
    private int eventMaxLength;
    private Set<Integer> eventStartMinutes;
    private int timeSlotSize;

    private Set<DayOfWeek> unavailableDays = Set.of(DayOfWeek.SATURDAY, DayOfWeek.SUNDAY);
    private DayOfWeek firstDayOfWeek = DayOfWeek.MONDAY;
    private DayOfWeek lastDayOfWeek;

    public OffsetDateTime startOfWeek(OffsetDateTime offsetDateTime) {
        OffsetDateTime firstDay = offsetDateTime.with(TemporalAdjusters.previousOrSame(this.firstDayOfWeek));
        return OffsetDateTime.of(firstDay.toLocalDate(), eventStartTime.toLocalTime(), eventStartTime.getOffset());
    }

    public OffsetDateTime endOfWeek(OffsetDateTime offsetDateTime) {
        if (lastDayOfWeek == null) {
            lastDayOfWeek = firstDayOfWeek.plus(WEEK_REMAINDER);
        }
        OffsetDateTime lastDay = offsetDateTime.with(TemporalAdjusters.nextOrSame(lastDayOfWeek));
        return OffsetDateTime.of(lastDay.toLocalDate(), eventEndTime.toLocalTime(), eventEndTime.getOffset());
    }

    public boolean isDayOfWeekAvailable(DayOfWeek dayOfWeek) {
        return !unavailableDays.contains(dayOfWeek);
    }
}
