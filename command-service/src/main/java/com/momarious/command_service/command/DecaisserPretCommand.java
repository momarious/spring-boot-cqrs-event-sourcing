package com.momarious.command_service.command;

import java.math.BigDecimal;

public record DecaisserPretCommand(String pretId, BigDecimal montantDecaisse) {
}
