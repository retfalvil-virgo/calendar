package hu.virgo.calendar.infrastructure.repository;

import hu.virgo.calendar.domain.model.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {

    Optional<Event> findByStartTimeLessThanAndEndTimeGreaterThan(OffsetDateTime endTime, OffsetDateTime startTime);

    List<Event> findByStartTimeGreaterThanEqualAndEndTimeLessThanEqual(OffsetDateTime startTime, OffsetDateTime endTime);

    @Query("""
            select event from Event event
            where event.startTime <= :endTime and event.endTime > :startTime
            """)
    Optional<Event> findByDateTime(@Param("startTime") OffsetDateTime startTime, @Param("endTime") OffsetDateTime endTime);
}
