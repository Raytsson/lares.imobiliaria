package br.imob.imovel.repository;

import br.imob.imovel.model.Imoveis;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface ImovelRepository extends JpaRepository<Imoveis, Long>, JpaSpecificationExecutor<Imoveis> {
}
