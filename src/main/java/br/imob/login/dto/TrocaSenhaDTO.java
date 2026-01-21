package br.imob.login.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record TrocaSenhaDTO(
        @NotBlank String senhaAtual,
        @NotBlank @Size(min = 6) String novaSenha
) {}
