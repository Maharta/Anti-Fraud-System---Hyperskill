package antifraud.presentation.DTO.user;

import javax.validation.constraints.NotBlank;

public record UserRequestDTO(@NotBlank String name, @NotBlank String username,
                             @NotBlank String password) {
}
