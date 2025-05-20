package com.momarious.query_service.event;

import java.time.LocalDateTime;
import java.util.UUID;

import com.momarious.query_service.enums.EventType;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class InboxEvent {

    private UUID id;

    private String source;

    private EventType type;

    private String payload;

    private Status status;

    private String error;

    private String processedBy;

    private int version;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    public enum Status {
        NEW,
        READY_FOR_PROCESSING,
        COMPLETED,
        ERROR
    }
}
