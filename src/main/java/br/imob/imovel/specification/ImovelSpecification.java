package br.imob.imovel.specification;

import br.imob.imovel.enums.Cidades;
import br.imob.imovel.enums.Status;
import br.imob.imovel.enums.TipoImovel;
import br.imob.imovel.model.Imoveis;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;
import jakarta.persistence.criteria.Predicate;


public class ImovelSpecification {

    public static Specification<Imoveis> tabela(
            String nome,
            TipoImovel tipoImovel,
            Status status,
            Cidades cidade
    ) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (nome != null && !nome.isBlank()) {
                predicates.add(
                        cb.like(
                                cb.lower(root.get("titulo")),
                                "%" + nome.toLowerCase() + "%"
                        )
                );
            }

            if (tipoImovel != null) {
                predicates.add(cb.equal(root.get("tipoImovel"), tipoImovel));
            }

            if (status != null) {
                predicates.add(cb.equal(root.get("status"), status));
            }

            if (cidade != null) {
                predicates.add(cb.equal(root.get("cidade"), cidade));
            }

            predicates.add(cb.isTrue(root.get("movelActive")));

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
