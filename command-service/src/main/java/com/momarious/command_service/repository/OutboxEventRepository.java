package com.momarious.command_service.repository;

import java.time.LocalDateTime;
import java.util.List;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import com.momarious.command_service.event.OutboxEvent;

import lombok.RequiredArgsConstructor;
import static com.momarious.command_service.db.model.Tables.OUTBOX_EVENTS;

@RequiredArgsConstructor
@Repository
public class OutboxEventRepository {

        private final DSLContext dbContext;

        public void batchUpdate(List<OutboxEvent> events) {

                dbContext.batch(
                                dbContext.insertInto(OUTBOX_EVENTS)
                                                .columns(
                                                                OUTBOX_EVENTS.AGGREGATE_ID,
                                                                OUTBOX_EVENTS.TYPE,
                                                                OUTBOX_EVENTS.AGGREGATE_TYPE,
                                                                OUTBOX_EVENTS.VERSION,
                                                                OUTBOX_EVENTS.PAYLOAD,
                                                                OUTBOX_EVENTS.CREATED_AT)
                                                .values((String) null, null, null, null, null, null))
                                .bind(
                                                events.stream()
                                                                .map(event -> new Object[] {
                                                                                event.getAggregateId(),
                                                                                event.getType(),
                                                                                event.getAggregateType(),
                                                                                event.getVersion(),
                                                                                event.getPayload(),
                                                                                event.getCreatedAt() != null
                                                                                                ? event.getCreatedAt()
                                                                                                : LocalDateTime.now()
                                                                })
                                                                .toArray(Object[][]::new))
                                .execute();

        }

}
