package br.imob.login.dto;

public record UsuarioResponseDTO(
        Long userId,
        String nome,
        String username,
        String email,
        String role
) {}