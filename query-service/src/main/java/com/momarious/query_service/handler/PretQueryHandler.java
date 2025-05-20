package com.momarious.query_service.handler;

import com.momarious.query_service.dto.PretResponse;
import com.momarious.query_service.event.Pret;
import com.momarious.query_service.exception.ResourceNotFoundException;
import com.momarious.query_service.query.GetAllPretsQuery;
import com.momarious.query_service.query.GetPretByIdQuery;
import com.momarious.query_service.query.GetPretsByStatutQuery;
import com.momarious.query_service.repository.PretRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Component
@AllArgsConstructor
public class PretQueryHandler {

  private final PretRepository repository;

  public Page<PretResponse> handle(GetAllPretsQuery query) {
    return repository
        .findAll(PageRequest.of(query.page(), query.size()))
        .map(this::toPretResponse);
  }

  public PretResponse handle(GetPretByIdQuery query) {
    return repository
        .findById(query.id())
        .map(this::toPretResponse)
        .orElseThrow(
            () -> new ResourceNotFoundException("Pret", "id", query.id()));
  }

  public List<PretResponse> handle(GetPretsByStatutQuery query) {
    return repository.findByStatut(query.statut()).stream()
        .map(this::toPretResponse)
        .toList();
  }

  private PretResponse toPretResponse(Pret pret) {
    UUID id = pret.getId();
    String clientId = pret.getClientId();
    BigDecimal montant = pret.getMontant();
    int dureeEnMois = pret.getDureeEnMois();
    String description = pret.getDescription();
    BigDecimal montantRembourse = pret.getMontantRembourse();
    String statut = pret.getStatut();

    return new PretResponse(id, clientId, montant, dureeEnMois, description,
        montantRembourse, statut);

  }
}
