package antifraud.presentation.DTO.user.update;

import antifraud.business.model.enums.StatusOperation;

import javax.validation.constraints.NotBlank;

public record UpdateStatusRequest(@NotBlank String username, StatusOperation operation) {
}
