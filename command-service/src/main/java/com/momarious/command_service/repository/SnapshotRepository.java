package com.momarious.command_service.repository;

import java.util.Optional;

import org.jooq.DSLContext;
import org.jooq.JSONB;
import org.springframework.stereotype.Repository;

import com.momarious.command_service.event.Snapshot;

import static com.momarious.command_service.db.model.Tables.SNAPSHOTS;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class SnapshotRepository {

        private final DSLContext dbContext;

        public void save(Snapshot snapshot) {
                dbContext.insertInto(SNAPSHOTS)
                                .set(SNAPSHOTS.AGGREGATE_ID, snapshot.getAggregateId())
                                .set(SNAPSHOTS.AGGREGATE_TYPE, snapshot.getAggregateType().name())
                                .set(SNAPSHOTS.DATA, snapshot.getData())
                                .set(SNAPSHOTS.VERSION, (int) snapshot.getVersion())
                                .onConflict(SNAPSHOTS.AGGREGATE_ID)
                                .doUpdate()
                                .set(SNAPSHOTS.AGGREGATE_TYPE, snapshot.getAggregateType().name())
                                .set(SNAPSHOTS.DATA, snapshot.getData())
                                .set(SNAPSHOTS.VERSION, (int) snapshot.getVersion())
                                // .returning()
                                // .fetchOne()
                                // .into(InboxEvent.class);
                                .execute();
        }

        public Optional<Snapshot> getSnapshotByAggregateId(String aggregateId) {
                return Optional.ofNullable(dbContext.select()
                                .from(SNAPSHOTS)
                                .where(SNAPSHOTS.AGGREGATE_ID.eq(aggregateId))
                                .limit(1)
                                .fetchOneInto(Snapshot.class));
        }

}
