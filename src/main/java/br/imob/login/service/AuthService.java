package br.imob.login.service;

import br.imob.login.dto.*;
import br.imob.login.model.Role;
import br.imob.login.repository.RoleRepository;
import br.imob.login.repository.UserRepository;
import br.imob.login.model.Usuario;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private JwtEncoder jwtEncoder;

    public LoginResponseDTO login(LoginRequestDTO loginRequest) {
        Usuario usuario = userRepository.findByUsername(loginRequest.username())
                .orElseThrow(() -> new BadCredentialsException("Usuário ou senha inválidos"));

        if (!usuario.isAtivo()) {
            throw new BadCredentialsException("O usuário está inativo. Entre em contato com o administrador.");
        }

        if (!passwordEncoder.matches(loginRequest.password(), usuario.getPassword())) {
            throw new BadCredentialsException("Usuário ou senha inválidos");
        }

        Instant now = Instant.now();
        long expiresIn = 3600L;

        String scopes = usuario.getRoles().stream()
                .map(role -> role.getName().toUpperCase())
                .collect(Collectors.joining(" "));

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("br.imob")
                .subject(usuario.getUserId().toString())
                .claim("username", usuario.getUsername())
                .claim("scope", scopes)
                .issuedAt(now)
                .expiresAt(now.plusSeconds(expiresIn))
                .build();

        String jwtValue = jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();

        return new LoginResponseDTO(jwtValue, expiresIn, "Bearer", usuario.getUsername(), usuario.getNome());
    }

    public UsuarioResponseDTO cadastrar(UsuarioRequestDTO dto) {
        if (userRepository.existsByUsername(dto.username())) {
            throw new RuntimeException("Nome de usuário já existe");
        }

        Usuario usuario = new Usuario();
        usuario.setNome(dto.nome());
        usuario.setUsername(dto.username());
        usuario.setEmail(dto.email());
        usuario.setAtivo(true);

        usuario.setPassword(passwordEncoder.encode(dto.password()));

        String roleName = dto.role() != null ? dto.role().toUpperCase() : "COLABORADOR";
        Role role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new RuntimeException("Role não encontrada: " + roleName));

        usuario.setRoles(Set.of(role));

        userRepository.save(usuario);

        return new UsuarioResponseDTO(
                usuario.getUserId(), usuario.getNome(), usuario.getUsername(),
                usuario.getEmail(), roleName
        );
    }

    @Transactional
    public void trocarSenha(Long userId, TrocaSenhaDTO dto) {
        Usuario usuario = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        if (!passwordEncoder.matches(dto.senhaAtual(), usuario.getPassword())) {
            throw new BadCredentialsException("A senha atual está incorreta.");
        }
        usuario.setPassword(passwordEncoder.encode(dto.novaSenha()));
        userRepository.save(usuario);
    }

    @Transactional
    public void resetarSenhaAdmin(Long userId, String novaSenhaDefinidaPeloAdmin) {
        Usuario usuario = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
        usuario.setPassword(passwordEncoder.encode(novaSenhaDefinidaPeloAdmin));
        userRepository.save(usuario);
    }


    @Transactional
    public void alterarStatusUsuario(Long userId, boolean ativo) {
        Usuario usuario = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        usuario.setAtivo(ativo);
        userRepository.save(usuario);
    }

    public List<UsuarioResponseDTO> listarTodos() {
        return userRepository.findAll().stream()
                .map(this::toResponseDTO) // Transforma cada entidade em DTO
                .toList();
    }

    private UsuarioResponseDTO toResponseDTO(Usuario usuario) {
        String rolesFormatadas = usuario.getRoles().stream()
                .map(Role::getName)
                .collect(Collectors.joining(", "));

        return new UsuarioResponseDTO(
                usuario.getUserId(),
                usuario.getNome(),
                usuario.getUsername(),
                usuario.getEmail(),
                rolesFormatadas
        );
    }
}