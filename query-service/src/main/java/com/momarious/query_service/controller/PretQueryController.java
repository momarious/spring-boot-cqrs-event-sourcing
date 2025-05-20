package com.momarious.query_service.controller;

import com.momarious.query_service.dto.PretResponse;
import com.momarious.query_service.service.PretQueryService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("api/v1/prets")
@RequiredArgsConstructor
public class PretQueryController {

  private static final Logger LOG = LoggerFactory.getLogger(PretQueryController.class);
  private final PretQueryService service;

  @GetMapping
  public ResponseEntity<Page<PretResponse>> getAllPrets(
      @RequestParam(defaultValue = "1") int page,
      @RequestParam(defaultValue = "10") int size) {
    LOG.info("Récupération de la liste des prêts,  page: {}, size: {}", page, size);
    Page<PretResponse> prets = service.getAllPrets(page, size);
    return ResponseEntity.ok(prets);
  }

  @GetMapping("{id}")
  public ResponseEntity<PretResponse> getPretById(@PathVariable UUID id) {
    LOG.info("Consultation du prêt, id: {}", id);
    PretResponse pret = service.getPretById(id);
    return ResponseEntity.ok(pret);
  }

  @GetMapping("statut/{statut}")
  public ResponseEntity<List<PretResponse>> getPretsByStatut(@PathVariable String statut) {
    LOG.info("Recherche des prêts, statut: {}", statut);
    List<PretResponse> prets = service.getPretsByStatut(statut);
    return ResponseEntity.ok(prets);
  }
}