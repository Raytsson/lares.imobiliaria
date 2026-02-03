package br.imob.imovel.dtos;

import br.imob.enderecos.dtos.EnderecosRequestDtos;
import br.imob.imovel.enums.Cidades;
import br.imob.imovel.enums.Status;
import br.imob.imovel.enums.TipoImovel;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record ImovelRequestDto (
        @NotBlank
        String titulo,
        String descricao,
        @NotNull
        TipoImovel tipoImovel,
        Status status,
        @NotNull @Positive
        BigDecimal valor,
        BigDecimal areaTotal,
        BigDecimal areaConstruida,
        int quartos,
        int banheiros,
        int vagasGaragem,
        Cidades cidade,
        EnderecosRequestDtos enderecos,
        boolean movelActive
){}
