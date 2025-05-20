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
public class OutboxEvent {

    private UUID id;

    private String aggregateId;

    private EventType type;

    private String payload;

    private AggregateType aggregateType;

    private long version;

    private LocalDateTime createdAt;
}