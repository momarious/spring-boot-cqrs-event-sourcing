package com.momarious.command_service.command;

import java.math.BigDecimal;

public record DemanderPretCommand(
                String pretId,
                String clientId,
                BigDecimal montant,
                int dureeEnMois,
                String description) {
}
