package com.momarious.command_service.event.aggregate;

import com.momarious.command_service.enums.AggregateType;
import com.momarious.command_service.enums.EventType;
import com.momarious.command_service.event.Event;
import com.momarious.command_service.exception.InvalidEventException;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Data
@NoArgsConstructor
public abstract class BaseAggregate {

  protected String id;
  protected AggregateType type;
  protected long version;
  protected final List<Event> changes = new ArrayList<>();

  protected BaseAggregate(final String id, final AggregateType aggregateType) {
    this.id = id;
    this.type = aggregateType;
  }

  public abstract void when(final Event event);

  public void load(final List<Event> events) {
    events.forEach(this::raiseEvent);
  }

  public void apply(final Event event) {
    this.validateEvent(event);
    event.setAggregateType(this.type);

    when(event);
    changes.add(event);

    this.version++;
    event.setVersion(this.version);
  }

  public void raiseEvent(final Event event) {
    this.validateEvent(event);

    event.setAggregateType(this.type);
    when(event);
    this.version++;
  }

  public void clearChanges() {
    this.changes.clear();
  }

  private void validateEvent(final Event event) {
    if (Objects.isNull(event) || !event.getAggregateId().equals(this.id))
      throw new InvalidEventException(
          event.toString());
  }

  protected Event createEvent(EventType eventType, byte[] data) {
    return Event.builder()
        .aggregateId(this.getId())
        .version(this.getVersion())
        .aggregateType(this.getType())
        .eventType(eventType)
        .data(Objects.isNull(data) ? new byte[] {} : data)
        .timestamp(LocalDateTime.now())
        .build();
  }

  public void toSnapshot() {
    this.clearChanges();
  }
}
