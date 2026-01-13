package br.imob.enderecos.repository;

import br.imob.enderecos.model.Enderecos;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface EnderecoRepository extends JpaRepository<Enderecos, UUID> {
}
