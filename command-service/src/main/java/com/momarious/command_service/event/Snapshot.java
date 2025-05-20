package com.momarious.command_service.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

import com.momarious.command_service.enums.AggregateType;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Snapshot {

    private UUID id;
    private String aggregateId;
    private AggregateType aggregateType;
    private byte[] data;
    private long version;
    private LocalDateTime timestamp;

    @Override
    public String toString() {
        return "Snapshot{" +
                "id=" + id +
                ", aggregateId='" + aggregateId + '\'' +
                ", aggregateType='" + aggregateType + '\'' +
                ", data=" + data.length + " bytes" +
                ", version=" + version +
                ", timestamp=" + timestamp +
                '}';
    }
}