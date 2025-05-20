package com.momarious.command_service.event.aggregate;

import com.momarious.command_service.command.ApprouverPretCommand;
import com.momarious.command_service.command.DecaisserPretCommand;
import com.momarious.command_service.command.DemanderPretCommand;
import com.momarious.command_service.command.RejetterPretCommand;
import com.momarious.command_service.command.RembourserPretCommand;
import com.momarious.command_service.enums.AggregateType;
import com.momarious.command_service.enums.EventType;
import com.momarious.command_service.event.Event;
import com.momarious.command_service.exception.AggregateStateException;
import com.momarious.command_service.exception.BadRequestException;
import com.momarious.command_service.exception.InvalidEventTypeException;
import com.momarious.command_service.exception.ProtobufParseException;

import events.PretEvents.PretDemandeEvent;
import events.PretEvents.PretApprouveEvent;
import events.PretEvents.PretPartiellementRembourseEvent;
import events.PretEvents.PretRejetteEvent;
import events.PretEvents.PretRembourseEvent;
import events.PretEvents.PretDecaisseEvent;
import events.PretEvents.StatutPret;
import lombok.*;

import java.io.IOException;
import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class PretAggregate extends BaseAggregate {

  private String clientId;
  private BigDecimal montant;
  private int dureeEnMois;
  private String description;
  private BigDecimal montantRembourse;
  private StatutPret statut;

  public PretAggregate(String id) {
    super(id, AggregateType.PRET_AGGREGATE);
  }

  @Override
  public void when(Event event) {
    try {

      switch (event.getEventType()) {
        case PRET_EN_ATTENTE ->
          handle(PretDemandeEvent.parseFrom(event.getData()));
        case PRET_APPROUVE ->
          handle(
              PretApprouveEvent.parseFrom(event.getData()));
        case PRET_REJETTE ->
          handle(PretRejetteEvent.parseFrom(event.getData()));
        case PRET_DECAISSE ->
          handle(
              PretDecaisseEvent.parseFrom(event.getData()));
        case PRET_REMBOURSE ->
          handle(
              PretRembourseEvent.parseFrom(event.getData()));
        case PRET_PARTIELLEMENT_REMBOURSE ->
          handle(
              PretPartiellementRembourseEvent.parseFrom(
                  event.getData()));
        default -> throw new InvalidEventTypeException(event.getEventType().toString());
      }
    } catch (IOException e) {
      throw new ProtobufParseException("Unable to parse protobuf bytes", e);

    }
  }

  private void handle(PretDemandeEvent event) {
    this.clientId = event.getClientId();
    this.montant = BigDecimal.valueOf(event.getMontant());
    this.dureeEnMois = event.getDureeEnMois();
    this.description = event.getDescription();
    this.statut = event.getStatut();
  }

  private void handle(PretApprouveEvent event) {
    this.statut = event.getStatut();
  }

  private void handle(PretRejetteEvent event) {
    this.statut = event.getStatut();
  }

  private void handle(PretDecaisseEvent event) {
    this.montant = BigDecimal.valueOf(event.getMontantDecaisse());
    this.montantRembourse = BigDecimal.valueOf(0);
    this.statut = event.getStatut();
  }

  private void handle(PretRembourseEvent event) {
    this.montantRembourse = this.montantRembourse.add(BigDecimal.valueOf(event.getMontantVerse()));
    this.statut = event.getStatut();
  }

  private void handle(PretPartiellementRembourseEvent event) {
    this.montantRembourse = this.montantRembourse.add(BigDecimal.valueOf(event.getMontantVerse()));
    this.statut = event.getStatut();
  }

  public void demanderPret(DemanderPretCommand command) {
    final PretDemandeEvent pretDemandeEvent = PretDemandeEvent.newBuilder()
        .setAggregateId(id)
        .setClientId(command.clientId())
        .setMontant(command.montant().doubleValue())
        .setDureeEnMois(command.dureeEnMois())
        .setDescription(command.description())
        .setStatut(StatutPret.EN_ATTENTE)
        .build();

    final Event event = this.createEvent(EventType.PRET_EN_ATTENTE, pretDemandeEvent.toByteArray());
    this.apply(event);
  }

  public void rejetterPret(RejetterPretCommand command) {
    if (StatutPret.REJETTE.equals(this.statut))
      throw new AggregateStateException("Prêt déjà rejetté");

    if (!StatutPret.EN_ATTENTE.equals(this.statut))
      throw new AggregateStateException("Le prêt doit être en attente");

    final PretRejetteEvent pretRejetteEvent = PretRejetteEvent.newBuilder()
        .setAggregateId(id)
        .setRaisonDuRejet(command.raisonDuRejet())
        .setStatut(StatutPret.REJETTE)
        .build();

    final Event event = this.createEvent(EventType.PRET_REJETTE, pretRejetteEvent.toByteArray());
    this.apply(event);
  }

  public void approuverPret(ApprouverPretCommand command) {
    if (StatutPret.APPROUVE.equals(this.statut))
      throw new AggregateStateException("Prêt déjà approuvé");

    if (!StatutPret.EN_ATTENTE.equals(this.statut))
      throw new AggregateStateException("Le prêt doit être en attente");

    final PretApprouveEvent pretApprouveEvent = PretApprouveEvent.newBuilder()
        .setAggregateId(id)
        .setStatut(StatutPret.APPROUVE)
        .build();

    final Event event = this.createEvent(EventType.PRET_APPROUVE, pretApprouveEvent.toByteArray());
    this.apply(event);
  }

  public void rembourserPret(RembourserPretCommand command) {
    BigDecimal montantVerse = command.montantVerse();
    BigDecimal montantRestant = this.montant.subtract(this.montantRembourse);

    if (montantRestant.compareTo(BigDecimal.ZERO) == 0)
      throw new BadRequestException("Vous avez remboursé l'intégralité du prêt.");

    if (montantVerse.compareTo(montantRestant) > 0)
      throw new BadRequestException("Le montant versé ne peut dépasser le montant dû.");

    boolean estTotalementRembourse = this.montantRembourse.add(montantVerse).compareTo(this.montant) == 0;

    if (estTotalementRembourse) {
      PretRembourseEvent pretRembourseEvent = PretRembourseEvent.newBuilder().setAggregateId(id)
          .setMontantVerse(montantVerse.doubleValue()).setStatut(StatutPret.REMBOURSE).build();

      Event event = this.createEvent(
          EventType.PRET_REMBOURSE,
          pretRembourseEvent.toByteArray());

      this.apply(event);

    } else {
      final PretPartiellementRembourseEvent pretPartiellementRembourseEvent = PretPartiellementRembourseEvent
          .newBuilder()
          .setAggregateId(id)
          .setMontantVerse(montantVerse.doubleValue())
          .setStatut(StatutPret.PARTIELLEMENT_REMBOURSE)
          .build();

      final Event event = this.createEvent(EventType.PRET_PARTIELLEMENT_REMBOURSE,
          pretPartiellementRembourseEvent.toByteArray());

      this.apply(event);
    }
  }

  public void decaisserPret(DecaisserPretCommand command) {
    if (StatutPret.DECAISSE.equals(this.statut))
      throw new AggregateStateException("Prêt déjà decaissé");

    if (!StatutPret.APPROUVE.equals(this.statut))
      throw new AggregateStateException("Le prêt doit être approuvé");

    if (command.montantDecaisse().compareTo(montant) > 0)
      throw new BadRequestException(
          "Le montant décaissé ne doit pas être supérieur au montant demandé.");

    final PretDecaisseEvent pretDecaisseEvent = PretDecaisseEvent.newBuilder()
        .setAggregateId(id)
        .setMontantDecaisse(command.montantDecaisse().doubleValue())
        .setStatut(StatutPret.DECAISSE)
        .build();

    final Event event = this.createEvent(EventType.PRET_DECAISSE, pretDecaisseEvent.toByteArray());
    this.apply(event);
  }

  @Override
  public String toString() {
    return "PretAggregate{"
        + "id='"
        + id
        + '\''
        + ", type='"
        + type
        + '\''
        + ", version="
        + version
        + ", clientId='"
        + clientId
        + '\''
        + ", montant="
        + montant
        + ", dureeEnMois="
        + dureeEnMois
        + ", description='"
        + description
        + '\''
        + ", montantRembourse="
        + montantRembourse
        + ", statut="
        + statut
        + ", changes="
        + changes.size()
        + '}';
  }
}
