package com.momarious.query_service.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record PretResponse(
        UUID id,
        String clientId,
        BigDecimal montant,
        int dureeEnMois,
        String description,
        BigDecimal montantRembourse,
        String statut) {
}
