package com.momarious.command_service.event.store;

import com.google.protobuf.Message;
import com.momarious.command_service.enums.EventType;
import com.momarious.command_service.event.Event;
import com.momarious.command_service.event.OutboxEvent;
import com.momarious.command_service.event.Snapshot;
import com.momarious.command_service.event.aggregate.BaseAggregate;
import com.momarious.command_service.exception.AggregateNotFoundException;
import com.momarious.command_service.exception.InvalidEventTypeException;
import com.momarious.command_service.repository.EventRepository;
import com.momarious.command_service.repository.OutboxEventRepository;
import com.momarious.command_service.repository.SnapshotRepository;
import com.momarious.command_service.utils.ESUtils;
import com.momarious.command_service.utils.ProtobufUtils;

import events.PretEvents;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class JooqEventStore implements EventStore {

    private static final Logger LOG = LoggerFactory.getLogger(JooqEventStore.class);
    private static final int SNAPSHOT_FREQUENCY = 3;

    private final OutboxEventRepository outboxEventRepository;
    private final SnapshotRepository snapshotRepository;
    private final EventRepository eventRepository;

    @Override
    public void saveEvents(List<Event> events) {
        if (events.isEmpty())
            return;

        if (events.size() > 1) {
            eventRepository.batchUpdate(events);
            return;
        }

        Event event = events.getFirst();
        eventRepository.update(event);
        LOG.info("Event saved, aggregateId: {}, version: {}", event.getAggregateId(), event.getVersion());
    }

    @Override
    public List<Event> loadEvents(String aggregateId, long version) {
        return eventRepository.getAllEventsByAggregateIdAndVersion(aggregateId, version);
    }

    @Transactional
    @Override
    public <T extends BaseAggregate> void save(T aggregate) {
        List<Event> changes = aggregate.getChanges();
        if (changes.isEmpty())
            return;

        if (aggregate.getVersion() > 1) {
            handleConcurrency(aggregate.getId());
        }

        saveEvents(changes);

        if (aggregate.getVersion() % SNAPSHOT_FREQUENCY == 0) {
            aggregate.toSnapshot();
            var a = ESUtils.snapshotFromAggregate(aggregate);
            snapshotRepository.save(a);
            LOG.info("Snapshot saved, aggregateId: {}", aggregate.getId());
        }
        saveOutboxEvents(changes);
    }

    private void saveOutboxEvents(List<Event> events) {
        if (events == null || events.isEmpty()) {
            return;
        }
        List<OutboxEvent> outboxEvents = events.stream()
                .filter(Objects::nonNull) // Filter out null events
                .map(this::maptoOutboxEvent)
                .toList();

        if (!outboxEvents.isEmpty()) {
            outboxEventRepository.batchUpdate(outboxEvents);
        }
    }

    private void handleConcurrency(String aggregateId) {
        try {
            String lockedId = eventRepository.lockAggregateById(aggregateId);
            LOG.info("Handling concurrency, aggregateId: {}, status: lock_attempted", lockedId);
        } catch (EmptyResultDataAccessException e) {
            LOG.error("Error handling concurrency, aggregateId: {}, error: {}", aggregateId, e.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public <T extends BaseAggregate> T load(String aggregateId, Class<T> aggregateType) {
        Optional<Snapshot> snapshot = snapshotRepository.getSnapshotByAggregateId(aggregateId);
        LOG.info("Load snapshot, aggregateId: {}, isPresent: {}", aggregateId, snapshot.isPresent());

        T aggregate = ESUtils.getSnapshotFromClass(snapshot, aggregateId, aggregateType);
        List<Event> events = loadEvents(aggregateId, aggregate.getVersion());
        LOG.info("Events loaded, aggregateId: {}, count: {}", aggregateId, events.size());

        aggregate.load(events);

        if (aggregate.getVersion() == 0) {
            LOG.error("Aggregate not found, aggregateId: {}", aggregateId);
            throw new AggregateNotFoundException(aggregateId);
        }

        return aggregate;
    }

    private OutboxEvent maptoOutboxEvent(Event event) {
        Message.Builder builder = resolveMessageBuilder(event.getEventType());

        return OutboxEvent.builder()
                .aggregateId(event.getAggregateId())
                .type(event.getEventType())
                .aggregateType(event.getAggregateType())
                .version(event.getVersion())
                .payload(ProtobufUtils.convertToJsonString(event.getData(), builder))
                .build();
    }

    private Message.Builder resolveMessageBuilder(EventType eventType) {
        return switch (eventType) {
            case PRET_EN_ATTENTE -> PretEvents.PretDemandeEvent.newBuilder();
            case PRET_REJETTE -> PretEvents.PretRejetteEvent.newBuilder();
            case PRET_APPROUVE -> PretEvents.PretApprouveEvent.newBuilder();
            case PRET_DECAISSE -> PretEvents.PretDecaisseEvent.newBuilder();
            case PRET_REMBOURSE -> PretEvents.PretRembourseEvent.newBuilder();
            case PRET_PARTIELLEMENT_REMBOURSE -> PretEvents.PretPartiellementRembourseEvent.newBuilder();
            default -> throw new InvalidEventTypeException("Unknown event type: " + eventType);
        };
    }
}