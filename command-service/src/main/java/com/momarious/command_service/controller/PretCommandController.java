package com.momarious.command_service.controller;

import com.momarious.command_service.dto.*;
import com.momarious.command_service.service.PretCommandService;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/prets")
@RequiredArgsConstructor
public class PretCommandController {

  private static final Logger LOG = LoggerFactory.getLogger(PretCommandController.class);
  private final PretCommandService service;

  @PostMapping
  public ResponseEntity<String> demanderPret(@RequestBody DemandePretRequest request) {
    LOG.info("DemandePretRequest, client: {}, montant: {}", request.clientId(), request.montant());
    service.demanderPret(request);
    return ResponseEntity.ok("Demande de prêt soumise avec succès.");
  }

  @PutMapping("{id}/approbation")
  public ResponseEntity<String> approuverPret(@PathVariable String id) {
    LOG.info("ApprouverPretRequest, id: {}", id);
    service.approuverPret(id);
    return ResponseEntity.ok("Prêt approuvé avec succès.");
  }

  @PutMapping("{id}/rejet")
  public ResponseEntity<String> rejeterPret(@PathVariable String id) {
    LOG.info("RejeterPretRequest, id: {}", id);
    service.rejeterPret(id);
    return ResponseEntity.ok("Prêt rejeté avec succès.");
  }

  @PutMapping("{id}/decaissement")
  public ResponseEntity<String> decaisserPret(
      @PathVariable String id, @RequestBody DecaissementPretRequest request) {
    LOG.info("DecaissementPretRequest, id: {}, montantDecaisse: {}", id, request.montantDecaisse());
    service.decaisserPret(id, request);
    return ResponseEntity.ok("Prêt décaissé avec succès.");
  }

  @PutMapping("{id}/remboursement")
  public ResponseEntity<String> rembourserPret(
      @PathVariable String id, @RequestBody RembourssementPretRequest request) {
    LOG.info("RembourssementPretRequest, id: {}, montantVerse: {}", id, request.montantVerse());
    service.rembourserPret(id, request);
    return ResponseEntity.ok("Remboursement enregistré avec succès.");
  }

}
