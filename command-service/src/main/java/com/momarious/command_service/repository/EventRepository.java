package com.momarious.command_service.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import com.momarious.command_service.event.Event;

import static com.momarious.command_service.db.model.Tables.EVENTS;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Repository
public class EventRepository {

        private final DSLContext dbContext;

        public List<Event> getAllEventsByAggregateIdAndVersion(String aggregateId, long version) {
                return dbContext.select().from(EVENTS)
                                .where(EVENTS.AGGREGATE_ID.eq(aggregateId))
                                .and(EVENTS.VERSION.greaterThan(version))
                                .orderBy(EVENTS.VERSION.asc())
                                .limit(100).fetchInto(Event.class);
        }

        public void update(Event event) {
                dbContext.insertInto(EVENTS)
                                .set(EVENTS.AGGREGATE_ID, event.getAggregateId())
                                .set(EVENTS.EVENT_TYPE, event.getEventType().name())
                                .set(EVENTS.AGGREGATE_TYPE, event.getAggregateType().name())
                                .set(EVENTS.VERSION, event.getVersion())
                                .set(EVENTS.DATA, event.getData())
                                .set(EVENTS.TIMESTAMP, event.getTimestamp())
                                .execute();

        }

        public void batchUpdate(List<Event> events) {

                dbContext.batch(
                                dbContext.insertInto(EVENTS)
                                                .columns(
                                                                EVENTS.AGGREGATE_ID,
                                                                EVENTS.EVENT_TYPE,
                                                                EVENTS.AGGREGATE_TYPE,
                                                                EVENTS.VERSION,
                                                                EVENTS.DATA,
                                                                EVENTS.TIMESTAMP)
                                                .values((String) null, null, null, 0L, null, null))
                                .bind(
                                                events.stream()
                                                                .map(event -> new Object[] {
                                                                                event.getAggregateId(),
                                                                                event.getEventType(),
                                                                                event.getAggregateType(),
                                                                                event.getVersion(),
                                                                                event.getData(),
                                                                                event.getTimestamp() != null
                                                                                                ? event.getTimestamp()
                                                                                                : LocalDateTime.now()
                                                                })
                                                                .toArray(Object[][]::new))
                                .execute();

        }

        public String lockAggregateById(String aggregateId) {
                return dbContext.select(EVENTS.AGGREGATE_ID)
                                .from(EVENTS)
                                .where(EVENTS.AGGREGATE_ID.eq(aggregateId))
                                .limit(1)
                                .forUpdate()
                                .fetchOne(EVENTS.AGGREGATE_ID);
        }
}
