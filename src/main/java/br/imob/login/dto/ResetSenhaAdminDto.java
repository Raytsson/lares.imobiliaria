package br.imob.login.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ResetSenhaAdminDto(
        @NotBlank @Size(min = 6) String novaSenha,
        String confirmacaoSenha
) {}