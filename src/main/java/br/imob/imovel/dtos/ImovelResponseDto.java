package br.imob.imovel.dtos;

import br.imob.imovel.enums.Status;
import br.imob.imovel.enums.TipoImovel;

import java.math.BigDecimal;
import java.util.List;

// DTO Principal (Sem o ID)
public record ImovelResponseDto(
        Long id,
        String titulo,
        String descricao,
        String cidade,
        TipoImovel tipoImovel,
        Status status,
        BigDecimal valor,
        BigDecimal areaTotal,
        BigDecimal areaConstruida,
        Integer quartos,
        Integer suites,
        Integer banheiros,
        Integer vagasGaragem,
        EnderecoDto endereco,
        List<String> urlsFotos,
        boolean isComercial
) {}

