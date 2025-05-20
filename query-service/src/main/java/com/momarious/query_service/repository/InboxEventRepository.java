package com.momarious.query_service.repository;

import static com.momarious.query_service.db.model.Tables.INBOX_EVENTS;

import java.util.List;
import org.jooq.DSLContext;
import org.jooq.JSONB;
import org.springframework.stereotype.Repository;

import com.momarious.query_service.event.InboxEvent;
import com.momarious.query_service.event.InboxEvent.Status;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Repository
public class InboxEventRepository {

    private static final int QUERY_HARD_LIMIT = 100;
    private final DSLContext dsl;

    public InboxEvent save(InboxEvent event) {
        return dsl.insertInto(INBOX_EVENTS)
                .set(INBOX_EVENTS.ID, event.getId())
                .set(INBOX_EVENTS.STATUS, event.getStatus().name())
                .set(INBOX_EVENTS.PAYLOAD, JSONB.valueOf(event.getPayload()))
                .set(INBOX_EVENTS.PROCESSED_BY, event.getProcessedBy())
                .set(INBOX_EVENTS.VERSION, event.getVersion())
                .onConflict(INBOX_EVENTS.ID)
                .doUpdate()
                .set(INBOX_EVENTS.STATUS, event.getStatus().name())
                .set(INBOX_EVENTS.PAYLOAD, JSONB.valueOf(event.getPayload()))
                .set(INBOX_EVENTS.PROCESSED_BY, event.getProcessedBy())
                .set(INBOX_EVENTS.VERSION, event.getVersion())
                .returning()
                .fetchOne()
                .into(InboxEvent.class);
    }

    public List<InboxEvent> findAllInboxEventsByStatus(Status statut) {
        return dsl.selectFrom(INBOX_EVENTS)
                .where(INBOX_EVENTS.STATUS.eq(statut.name()))
                .fetchInto(InboxEvent.class);
    }

}
