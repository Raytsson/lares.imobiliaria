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
        // 1. Buscar usuário
        Usuario usuario = userRepository.findByUsername(loginRequest.username())
                .orElseThrow(() -> new BadCredentialsException("Usuário ou senha inválidos"));

        // 2. Validar senha
        if (!passwordEncoder.matches(loginRequest.password(), usuario.getPassword())) {
            throw new BadCredentialsException("Usuário ou senha inválidos");
        }

        // 3. Gerar Token JWT
        Instant now = Instant.now();
        long expiresIn = 3600L; // 1 hora

        // Escopos (Roles)
        String scopes = usuario.getRoles().stream()
                .map(role -> role.getName().toUpperCase()) // ADMIN, GERENTE
                .collect(Collectors.joining(" "));

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("br.imob")
                .subject(usuario.getUserId().toString())
                .claim("username", usuario.getUsername())
                .claim("scope", scopes) // O SecurityConfig vai ler isso aqui
                .issuedAt(now)
                .expiresAt(now.plusSeconds(expiresIn))
                .build();

        String jwtValue = jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();

        return new LoginResponseDTO(jwtValue, expiresIn, "Bearer", usuario.getUsername(), usuario.getNome());
    }

    public UsuarioResponseDTO cadastrar(UsuarioRequestDTO dto) {
        // 1. Verificar se já existe
        if (userRepository.existsByUsername(dto.username())) {
            throw new RuntimeException("Nome de usuário já existe");
        }

        // 2. Criar entidade
        Usuario usuario = new Usuario();
        usuario.setNome(dto.nome());
        usuario.setUsername(dto.username());
        usuario.setEmail(dto.email());
        usuario.setAtivo(true); // Usuário nasce ativo

        // 3. Criptografar Senha (MUITO IMPORTANTE)
        usuario.setPassword(passwordEncoder.encode(dto.password()));

        // 4. Definir Role (Perfil)
        // Se vier nulo, assume "COLABORADOR" ou busca do banco
        String roleName = dto.role() != null ? dto.role().toUpperCase() : "COLABORADOR";
        Role role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new RuntimeException("Role não encontrada: " + roleName));

        usuario.setRoles(Set.of(role));

        // 5. Salvar e Retornar
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
    public void alterarStatusUsuario(Long userId, boolean ativo) {
        Usuario usuario = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        usuario.setAtivo(ativo);
        userRepository.save(usuario);
    }
}