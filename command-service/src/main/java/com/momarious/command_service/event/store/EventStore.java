package com.momarious.command_service.event.store;

import java.util.List;

import com.momarious.command_service.event.Event;
import com.momarious.command_service.event.aggregate.BaseAggregate;

public interface EventStore {

    void saveEvents(final List<Event> events);

    List<Event> loadEvents(final String aggregateId, long version);

    <T extends BaseAggregate> void save(final T aggregate);

    <T extends BaseAggregate> T load(final String aggregateId, final Class<T> aggregateType);

}
