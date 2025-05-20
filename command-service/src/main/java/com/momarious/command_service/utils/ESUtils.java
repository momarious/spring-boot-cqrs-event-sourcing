package com.momarious.command_service.utils;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import com.momarious.command_service.event.Snapshot;
import com.momarious.command_service.event.aggregate.BaseAggregate;

public final class ESUtils {

    private ESUtils() {
    }

    public static <T extends BaseAggregate> Snapshot snapshotFromAggregate(T aggregate) {
        return Snapshot.builder()
                .id(UUID.randomUUID())
                .aggregateId(aggregate.getId())
                .aggregateType(aggregate.getType())
                .version(aggregate.getVersion())
                .data(SerializerUtils.serializeToJsonBytes(aggregate))
                .timestamp(LocalDateTime.now())
                .build();
    }

    private static <T extends BaseAggregate> T aggregateFromSnapshot(Snapshot snapshot, Class<T> valueType) {
        return SerializerUtils.deserializeFromJsonBytes(snapshot.getData(),
                valueType);
    }

    public static <T extends BaseAggregate> T getSnapshotFromClass(
            Optional<Snapshot> snapshot, String aggregateId, Class<T> aggregateType) {
        return snapshot.map(s -> aggregateFromSnapshot(s, aggregateType))
                .orElseGet(() -> {
                    T newAggregate = createAggregate(aggregateId, aggregateType);
                    Snapshot s = snapshotFromAggregate(newAggregate);
                    return aggregateFromSnapshot(s, aggregateType);
                });
    }

    private static <T extends BaseAggregate> T createAggregate(String aggregateId, Class<T> aggregateType) {
        try {
            return aggregateType.getConstructor(String.class).newInstance(aggregateId);
        } catch (Exception ex) {
            throw new IllegalArgumentException("Failed to create aggregate: " + aggregateType.getSimpleName(), ex);
        }
    }

}