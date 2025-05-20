package com.momarious.command_service.service;

import com.momarious.command_service.command.*;
import com.momarious.command_service.dto.DecaissementPretRequest;
import com.momarious.command_service.dto.DemandePretRequest;
import com.momarious.command_service.dto.RembourssementPretRequest;
import com.momarious.command_service.exception.BadRequestException;
import com.momarious.command_service.handler.PretCommandHandler;
import lombok.AllArgsConstructor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.UUID;

@AllArgsConstructor
@Service
public class PretCommandService {

  private static final Logger LOG = LoggerFactory.getLogger(PretCommandService.class);
  private final PretCommandHandler handler;

  public void demanderPret(DemandePretRequest request) {

    if (request.dureeEnMois() > 12) {
      LOG.error("Invalid pret duration: {} months. Maximum allowed is 12 months.", request.dureeEnMois());
      throw new BadRequestException("La durée du prêt ne peut être supérieure à 12 mois.");
    }
    //
    String aggregateId = String.valueOf(UUID.randomUUID());
    DemanderPretCommand command = new DemanderPretCommand(aggregateId, request.clientId(), request.montant(),
        request.dureeEnMois(), request.description());
    handler.handle(command);
  }

  public void rejeterPret(String pretId) {
    //
    RejetterPretCommand command = new RejetterPretCommand(pretId, "raisonDuRejet");
    handler.handle(command);
  }

  public void approuverPret(String pretId) {
    //
    ApprouverPretCommand command = new ApprouverPretCommand(pretId);
    handler.handle(command);
  }

  public void decaisserPret(String pretId, DecaissementPretRequest request) {
    //
    DecaisserPretCommand command = new DecaisserPretCommand(pretId, request.montantDecaisse());
    handler.handle(command);
  }

  public void rembourserPret(String pretId, RembourssementPretRequest request) {
    //
    final RembourserPretCommand command = new RembourserPretCommand(pretId, request.montantVerse());
    handler.handle(command);
  }
}
