package com.momarious.command_service.enums;

import lombok.Getter;

@Getter
public enum EventType {
  PRET_EN_ATTENTE,
  PRET_APPROUVE,
  PRET_REJETTE,
  PRET_DECAISSE,
  PRET_PARTIELLEMENT_REMBOURSE,
  PRET_REMBOURSE
}
