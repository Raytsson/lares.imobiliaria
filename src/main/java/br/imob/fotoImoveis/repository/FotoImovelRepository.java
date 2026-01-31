package br.imob.fotoImoveis.repository;

import br.imob.fotoImoveis.FotoImovel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FotoImovelRepository extends JpaRepository<FotoImovel, Integer> {
    List<FotoImovel> findByImovelId(Long imovelId);
}
