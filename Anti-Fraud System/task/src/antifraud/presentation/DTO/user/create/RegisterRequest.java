package antifraud.presentation.DTO.user.create;

import javax.validation.constraints.NotBlank;

public record RegisterRequest(@NotBlank String name, @NotBlank String username,
                              @NotBlank String password) {
}
