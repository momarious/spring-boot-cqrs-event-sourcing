package com.momarious.command_service.exception;

public class AggregateNotFoundException extends RuntimeException {
    public AggregateNotFoundException() {
        super();
    }

    public AggregateNotFoundException(String aggregateID) {
        super("Aggregate not found with id: " + aggregateID);
    }
}
