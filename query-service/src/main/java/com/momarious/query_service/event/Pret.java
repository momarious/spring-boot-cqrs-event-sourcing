package com.momarious.query_service.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Pret {

  private UUID id;

  private String clientId;

  private BigDecimal montant;

  private int dureeEnMois;

  private String description;

  private BigDecimal montantRembourse;

  private String statut;

  private LocalDateTime createdAt;

  private LocalDateTime updatedAt;

}
