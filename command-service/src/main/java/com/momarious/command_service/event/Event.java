package com.momarious.command_service.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

import com.momarious.command_service.enums.AggregateType;
import com.momarious.command_service.enums.EventType;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Event {
    private UUID id;
    private String aggregateId;
    private EventType eventType;
    private AggregateType aggregateType;
    private long version;
    private byte[] data;
    private LocalDateTime timestamp;

    public Event(EventType eventType, AggregateType aggregateType) {
        this.eventType = eventType;
        this.aggregateType = aggregateType;
    }

    @Override
    public String toString() {
        return "Event{"
                + "id="
                + id
                + ", aggregateId='"
                + aggregateId
                + '\''
                + ", eventType='"
                + eventType
                + '\''
                + ", aggregateType='"
                + aggregateType
                + '\''
                + ", version="
                + version
                + '\''
                + ", timeStamp="
                + timestamp
                + '\''
                + ", data="
                + new String(data)
                + '\''
                + '}';
    }
}