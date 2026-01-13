package br.imob.login.dto;

public record LoginResponseDTO(
        String accessToken,
        Long expiresIn,
        String tokenType, // Geralmente "Bearer"
        String username,
        String name // Nome real do usuário para exibir na tela ("Olá, Fulano")
) {}