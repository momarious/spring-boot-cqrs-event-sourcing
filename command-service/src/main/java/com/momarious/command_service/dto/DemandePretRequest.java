package com.momarious.command_service.dto;

import java.math.BigDecimal;

public record DemandePretRequest(
        String clientId, BigDecimal montant, int dureeEnMois, String description) {
}
