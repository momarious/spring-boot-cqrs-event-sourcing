package com.momarious.query_service.event.projection;

import com.google.protobuf.InvalidProtocolBufferException;
import com.momarious.query_service.enums.EventType;
import com.momarious.query_service.event.InboxEvent;
import com.momarious.query_service.event.Pret;
import com.momarious.query_service.event.InboxEvent.Status;
import com.momarious.query_service.exception.EventProcessingException;
import com.momarious.query_service.exception.InvalidEventTypeException;
import com.momarious.query_service.exception.ProtobufParseException;
import com.momarious.query_service.exception.ResourceNotFoundException;
import com.momarious.query_service.repository.InboxEventRepository;
import com.momarious.query_service.repository.PretRepository;
import com.momarious.query_service.utils.ProtobufUtils;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import events.PretEvents.PretDemandeEvent;
import events.PretEvents.PretApprouveEvent;
import events.PretEvents.PretPartiellementRembourseEvent;
import events.PretEvents.PretRejetteEvent;
import events.PretEvents.PretRembourseEvent;
import events.PretEvents.PretDecaisseEvent;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class PretProjection implements Projection {

    private static final Logger LOG = LoggerFactory.getLogger(PretProjection.class);
    private final PretRepository pretJooqRepository;
    private final InboxEventRepository inboxEventRepository;

    @Transactional
    @Scheduled(fixedDelay = 1000)
    public void projectEvents() {
        List<InboxEvent> events = inboxEventRepository.findAllInboxEventsByStatus(Status.NEW);
        for (InboxEvent event : events) {
            LOG.info("Traitement de l'événement, id: {}", event.getId());
            try {

                when(event);
                event.setStatus(Status.COMPLETED);
                event.setProcessedBy("query-service");
                inboxEventRepository.save(event);
                LOG.info("Événement traité avec succès, id: {}", event.getId());

            } catch (Exception e) {
                LOG.error("Erreur lors du traitement de l'événement, id: {}, Error: {}", event.getId(), e.getMessage(),
                        e);
                event.setStatus(Status.ERROR);
                event.setError(e.getMessage());
                inboxEventRepository.save(event);
            }
        }
    }

    @Override
    public void when(InboxEvent event) {
        byte[] data = resolveMessageBuilder(event.getType(), event.getPayload());

        try {
            switch (event.getType()) {
                case PRET_EN_ATTENTE -> handle(PretDemandeEvent.parseFrom(data));
                case PRET_APPROUVE -> handle(PretApprouveEvent.parseFrom(data));
                case PRET_REJETTE -> handle(PretRejetteEvent.parseFrom(data));
                case PRET_DECAISSE -> handle(PretDecaisseEvent.parseFrom(data));
                case PRET_REMBOURSE -> handle(PretRembourseEvent.parseFrom(data));
                case PRET_PARTIELLEMENT_REMBOURSE -> handle(PretPartiellementRembourseEvent.parseFrom(data));
                default -> throw new InvalidEventTypeException(event.getType());
            }
        } catch (InvalidProtocolBufferException e) {
            throw new ProtobufParseException(
                    String.format("Erreur de parsing Protobuf pour l'événement: %s", event.getType()), e);
        } catch (Exception e) {
            throw new EventProcessingException(
                    String.format("Erreur inattendue lors du traitement de l'événement %s", event.getType()), e);
        }

    }

    private void handle(PretDemandeEvent event) {
        final Pret pret = Pret.builder()
                .id(UUID.fromString(event.getAggregateId()))
                .montant(BigDecimal.valueOf(event.getMontant()))
                .dureeEnMois(event.getDureeEnMois())
                .description(event.getDescription())
                .statut(event.getStatut().name())
                .clientId(event.getClientId())
                .montantRembourse(BigDecimal.ZERO)
                .build();

        pretJooqRepository.save(pret);
        LOG.info("Prêt crée avec success, id: {}", pret.getId());
    }

    private void handle(PretDecaisseEvent event) {
        final Pret pret = getPretById(UUID.fromString(event.getAggregateId()));
        pret.setStatut(event.getStatut().name());
        pret.setMontant(BigDecimal.valueOf(event.getMontantDecaisse()));
        pret.setMontantRembourse(BigDecimal.ZERO);
        pretJooqRepository.save(pret);
        LOG.info("Prêt décaissé, id: {}, montantDecaisse: {}", pret.getId(), pret.getMontant());
    }

    private void handle(PretRembourseEvent event) {
        final Pret pret = getPretById(UUID.fromString(event.getAggregateId()));
        pret.setMontantRembourse(pret.getMontantRembourse().add(BigDecimal.valueOf(event.getMontantVerse())));
        pret.setStatut(event.getStatut().name());
        pretJooqRepository.save(pret);
        LOG.info("Prêt entièrement remboursé, id: {}, montantRembourse: {}", pret.getId(),
                pret.getMontantRembourse());
    }

    private void handle(PretPartiellementRembourseEvent event) {
        final Pret pret = getPretById(UUID.fromString(event.getAggregateId()));
        pret.setMontantRembourse(pret.getMontantRembourse().add(BigDecimal.valueOf(event.getMontantVerse())));
        pret.setStatut(event.getStatut().name());
        pretJooqRepository.save(pret);
        LOG.info("Prêt partiellement remboursé, id: {}, montantVerse: {}, montantRembourse: {}", pret.getId(),
                event.getMontantVerse(), pret.getMontantRembourse());
    }

    private void handle(PretRejetteEvent event) {
        final Pret pret = getPretById(UUID.fromString(event.getAggregateId()));
        pret.setStatut(event.getStatut().name());
        pretJooqRepository.save(pret);
        LOG.info("Prêt rejeté, id: {}", pret.getId());
    }

    private void handle(PretApprouveEvent event) {
        final Pret pret = getPretById(UUID.fromString(event.getAggregateId()));
        pret.setStatut(event.getStatut().name());
        pretJooqRepository.save(pret);
        LOG.info("Prêt approuvé, id: {}", pret.getId());
    }

    private Pret getPretById(UUID id) {
        return pretJooqRepository
                .findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pret", "id", id));
    }

    private byte[] resolveMessageBuilder(EventType eventType, String json) {
        return switch (eventType) {
            case PRET_EN_ATTENTE ->
                ProtobufUtils.convertJsonStringToProtobuf(json, PretDemandeEvent.newBuilder()).toByteArray();
            case PRET_APPROUVE ->
                ProtobufUtils.convertJsonStringToProtobuf(json, PretApprouveEvent.newBuilder()).toByteArray();
            case PRET_REJETTE ->
                ProtobufUtils.convertJsonStringToProtobuf(json, PretRejetteEvent.newBuilder()).toByteArray();
            case PRET_DECAISSE ->
                ProtobufUtils.convertJsonStringToProtobuf(json, PretDecaisseEvent.newBuilder()).toByteArray();
            case PRET_REMBOURSE ->
                ProtobufUtils.convertJsonStringToProtobuf(json, PretRembourseEvent.newBuilder()).toByteArray();
            case PRET_PARTIELLEMENT_REMBOURSE ->
                ProtobufUtils.convertJsonStringToProtobuf(json, PretPartiellementRembourseEvent.newBuilder())
                        .toByteArray();
            default -> throw new InvalidEventTypeException(eventType);
        };
    }

}
