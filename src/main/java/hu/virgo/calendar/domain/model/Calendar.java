package hu.virgo.calendar.domain.model;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.DayOfWeek;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.ZoneOffset;
import java.time.temporal.TemporalAdjusters;
import java.util.Set;

@Data
@ConfigurationProperties("hu.virgo.calendar")
public class Calendar {

    private static final int WEEK_REMAINDER = 6;

    private int startHour;
    private int endHour;
    private int endMinute;
    private int startMinute;
    private int minEventLength;
    private int maxEventLength;
    private Set<Integer> eventStartMinutes;
    private int timeSlotSize;

    private Set<DayOfWeek> unavailableDays = Set.of(DayOfWeek.SATURDAY, DayOfWeek.SUNDAY);
    private OffsetTime startOfDay;
    private OffsetTime endOfDay;

    private DayOfWeek firstDayOfWeek = DayOfWeek.MONDAY;
    private DayOfWeek lastDayOfWeek;

    public OffsetDateTime startOfWeek(OffsetDateTime offsetDateTime) {
        return offsetDateTime
                .with(TemporalAdjusters.previousOrSame(firstDayOfWeek))
                .withHour(startHour)
                .withMinute(startMinute);
    }

    public OffsetDateTime endOfWeek(OffsetDateTime offsetDateTime) {
        if (lastDayOfWeek == null) {
            lastDayOfWeek = firstDayOfWeek.plus(WEEK_REMAINDER);
        }
        return offsetDateTime
                .with(TemporalAdjusters.nextOrSame(lastDayOfWeek))
                .withHour(endHour)
                .withMinute(endMinute);
    }

    public OffsetTime getStartOfDay() {
        if (startOfDay == null) {
            startOfDay = OffsetTime.of(startHour, startMinute, 0, 0, ZoneOffset.UTC);
        }
        return startOfDay;
    }

    public OffsetTime getEndOfDay() {
        if (endOfDay == null) {
            endOfDay = OffsetTime.of(endHour, endMinute, 0, 0, ZoneOffset.UTC);
        }
        return endOfDay;
    }

    public boolean isDayOfWeekAvailable(DayOfWeek dayOfWeek) {
        return !unavailableDays.contains(dayOfWeek);
    }
}
