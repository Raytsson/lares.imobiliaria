package br.imob.login.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UsuarioRequestDTO(
        @NotBlank
        String nome,
        @NotBlank @Size(min = 5, message = "O usuário deve ter no mínimo 5 caracteres")
        String username,
        @NotBlank @Email
        String email,
        @NotBlank @Size(min = 6, message = "A senha deve ter no mínimo 6 caracteres")
        String password,
        String role
) {}
