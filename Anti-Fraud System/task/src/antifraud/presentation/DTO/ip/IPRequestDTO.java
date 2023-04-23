package antifraud.presentation.DTO.ip;

import antifraud.presentation.validation.ValidIP;

public record IPRequestDTO(@ValidIP String ip) {
}
