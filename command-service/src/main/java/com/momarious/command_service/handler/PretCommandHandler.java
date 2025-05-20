package com.momarious.command_service.handler;

import com.momarious.command_service.command.*;
import com.momarious.command_service.event.aggregate.PretAggregate;
import com.momarious.command_service.event.store.JooqEventStore;

import lombok.AllArgsConstructor;

import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class PretCommandHandler {

        private final JooqEventStore eventStore;

        public void handle(DemanderPretCommand command) {
                final PretAggregate aggregate = new PretAggregate(command.pretId());
                aggregate.demanderPret(command);
                eventStore.save(aggregate);
        }

        public void handle(RejetterPretCommand command) {
                final var aggregate = eventStore.load(command.pretId(), PretAggregate.class);
                aggregate.rejetterPret(command);
                eventStore.save(aggregate);
        }

        public void handle(ApprouverPretCommand command) {
                final PretAggregate aggregate = eventStore.load(command.pretId(), PretAggregate.class);
                aggregate.approuverPret(command);
                eventStore.save(aggregate);
        }

        public void handle(DecaisserPretCommand command) {
                final PretAggregate aggregate = eventStore.load(command.pretId(), PretAggregate.class);
                aggregate.decaisserPret(command);
                eventStore.save(aggregate);
        }

        public void handle(RembourserPretCommand command) {
                final PretAggregate aggregate = eventStore.load(command.pretId(), PretAggregate.class);
                aggregate.rembourserPret(command);
                eventStore.save(aggregate);
        }
}
