package br.imob.login;

import br.imob.login.dto.*;
import br.imob.login.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    // --- Endpoints Públicos ---
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody LoginRequestDTO loginRequest) {
        LoginResponseDTO response = authService.login(loginRequest);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/usuarios")
    public ResponseEntity<UsuarioResponseDTO> cadastrar(@RequestBody @Valid UsuarioRequestDTO dto) {
        UsuarioResponseDTO novoUsuario = authService.cadastrar(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(novoUsuario);
    }

    // --- Endpoints Protegidos (Exigem Token) ---
    @PatchMapping("/senha")
    @Operation(summary = "Trocar senha do usuário logado")
    @SecurityRequirement(name = "bearer-key")
    public ResponseEntity<Void> trocarSenha(
            @RequestBody @Valid TrocaSenhaDTO dto,
            Authentication authentication
    ) {
        Jwt jwt = (Jwt) authentication.getPrincipal();
        Long userId = Long.valueOf(jwt.getSubject());

        authService.trocarSenha(userId, dto);

        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{usuarioId}/reset-senha-admin")
    @Operation(summary = "Admin reseta a senha de um usuário específico")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> resetarSenhaPeloAdmin(
            @PathVariable Long usuarioId,
            @RequestBody @Valid ResetSenhaAdminDto dto
    ) {
        if (dto.confirmacaoSenha() != null && !dto.novaSenha().equals(dto.confirmacaoSenha())) {
            throw new IllegalArgumentException("As senhas não conferem.");
        }

        authService.resetarSenhaAdmin(usuarioId, dto.novaSenha());

        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    @Operation(summary = "Ativar ou Desativar um usuário (Admin/Gerente)")
    @SecurityRequirement(name = "bearer-key")
    public ResponseEntity<Void> alterarStatus(
            @PathVariable Long id,
            @RequestParam boolean ativo
    ) {
        authService.alterarStatusUsuario(id, ativo);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    @Operation(summary = "Lista todos os usuários cadastrados")
    @PreAuthorize("hasRole('ADMIN')") // Sugestão: Só admin vê a lista completa
    public ResponseEntity<List<UsuarioResponseDTO>> listarTodos() {
        return ResponseEntity.ok(authService.listarTodos());
    }
}