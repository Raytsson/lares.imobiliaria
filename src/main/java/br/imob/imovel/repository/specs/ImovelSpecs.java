package br.imob.imovel.repository.specs;

import br.imob.imovel.enums.Cidades;
import br.imob.imovel.model.Imoveis;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class ImovelSpecs {
    public static Specification<Imoveis> comFiltros(Cidades cidades,Integer quartos, Integer vagas, String bairro, BigDecimal valorMin, BigDecimal valorMax) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            predicates.add(cb.isTrue(root.get("movelActive")));

            if(cidades != null){predicates.add(cb.equal(root.get("cidade"), cidades));}
            if (quartos != null) predicates.add(cb.equal(root.get("quartos"), quartos));
            if (vagas != null) predicates.add(cb.equal(root.get("vagasGaragem"), vagas));

            if (bairro != null && !bairro.isEmpty()) {
                predicates.add(cb.like(cb.lower(root.get("endereco").get("bairro")), "%" + bairro.toLowerCase() + "%"));
            }
            if (valorMin != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("valor"), valorMin));
            }
            if (valorMax != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("valor"), valorMax));
            }


            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
