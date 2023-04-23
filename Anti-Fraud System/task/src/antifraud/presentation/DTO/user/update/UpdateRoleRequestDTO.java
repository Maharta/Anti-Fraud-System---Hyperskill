package antifraud.presentation.DTO.user.update;

import javax.validation.constraints.NotBlank;

public record UpdateRoleRequestDTO(@NotBlank String username, @NotBlank String role) {
}
