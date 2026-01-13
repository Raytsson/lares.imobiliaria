package br.imob.login;

import br.imob.login.dto.LoginRequestDTO;
import br.imob.login.dto.LoginResponseDTO;
import br.imob.login.dto.UsuarioRequestDTO;
import br.imob.login.dto.UsuarioResponseDTO;
import br.imob.login.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

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
}
