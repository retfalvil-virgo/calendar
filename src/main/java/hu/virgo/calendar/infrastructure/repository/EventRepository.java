package hu.virgo.calendar.infrastructure.repository;

import hu.virgo.calendar.domain.model.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {

    Optional<Event> findByStartTimeLessThanAndEndTimeGreaterThan(OffsetDateTime startTime, OffsetDateTime endTime);

    List<Event> findByStartTimeGreaterThanEqualAndEndTimeLessThanEqual(OffsetDateTime startTime, OffsetDateTime endTime);
}
