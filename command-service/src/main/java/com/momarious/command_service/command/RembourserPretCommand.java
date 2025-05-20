package com.momarious.command_service.command;

import java.math.BigDecimal;

public record RembourserPretCommand(String pretId, BigDecimal montantVerse) {
}
